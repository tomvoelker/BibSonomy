package org.bibsonomy.recommender;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.comparators.RecommendedTagComparator;
import org.bibsonomy.recommender.tags.TagRecommenderConnector;
import org.bibsonomy.recommender.tags.database.DBAccess;
import org.bibsonomy.recommender.tags.database.DBLogic;
import org.bibsonomy.recommender.tags.multiplexer.MultiplexingTagRecommender;
import org.bibsonomy.recommender.tags.multiplexer.RecommendedTagResultManager;
import org.bibsonomy.recommender.tags.simple.DummyTagRecommender;
import org.bibsonomy.recommender.testutil.JNDITestDatabaseBinder;
import org.bibsonomy.recommender.testutil.SelectCounter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author fei
 * @version $Id$
 */
public class MultiplexingTagRecommenderTest {
	private static final Log log = LogFactory.getLog(MultiplexingTagRecommenderTest.class);
	private DBLogic dbLogic;
	private static final int NROFRECOS = 10;
	private static final int MSTOWAIT = 1000;
	private static final int MAXSTOREITERATIONS  = 100;
	private static final int MAXSTORERECOMMENDER = 50;
	private static final int MAXSTORENROFTAGS    = 5;

	//------------------------------------------------------------------------
	// junit setup
	//------------------------------------------------------------------------

	/**
	 * Method for interactive testing.
	 */
	public static void main( String[] args ) throws Exception {
		DBAccessTest obj = new DBAccessTest();
		JNDITestDatabaseBinder.bind();
		/*
		obj.testAddNewSelector2();
		obj.testAddNewSelector();
		obj.testAddSelectedTags();
		obj.testAddQuery();
		*/
		obj.testGetPostDataForQuery();
		JNDITestDatabaseBinder.unbind();
    }
	
	@Before
	public void setUp() {
		// bind datasource access via JNDI
		JNDITestDatabaseBinder.bind();
		dbLogic = DBAccess.getInstance();
	}
	
	@After
	public void tearDown() {
		JNDITestDatabaseBinder.unbind();
	}
	
	//------------------------------------------------------------------------
	// test cases
	//------------------------------------------------------------------------
	public class ResultStoreProducer extends Thread {
		RecommendedTagResultManager store;
		private final int nrOfTags;
		private final Long qid;
		private final Long sid;
		private final long timeout;
		
		public ResultStoreProducer(RecommendedTagResultManager store, 
				Long qid, Long sid, int nrOfTags, long timeout) {
			this.store    = store;
			this.nrOfTags = nrOfTags;
			this.qid      = qid;
			this.sid      = sid;
			this.timeout  = timeout;
		}
		
		public void run() {
			SortedSet<RecommendedTag> result = 
				new TreeSet<RecommendedTag>(new RecommendedTagComparator());
			for( int i=0; i<nrOfTags; i++ )
				result.add(new RecommendedTag("TAG_"+i,1.0*i/(nrOfTags+1),0.5));
			
			try {
				Thread.sleep(timeout);
			} catch (Exception ex) {
			}
			
			store.addResult(qid, sid, result);
		}
	}
	
	/**
	 * Test the concurrent result cache 
	 * @throws Exception 
	 */
	@Test
	public void testResultStore() {
		// generate a set with different random ids 
		Set<Long> queryIDs = new TreeSet<Long>();
		while( queryIDs.size()<MAXSTOREITERATIONS )
			 queryIDs.add(Math.round(Math.random()*(Long.MAX_VALUE)));
		
		// create store to test
		RecommendedTagResultManager store = new RecommendedTagResultManager();
		
		//
		// spawn MAXSTORERECOMMENDER for each query id
		//
		Collection<ResultStoreProducer> producers = new LinkedList<ResultStoreProducer>();
		Iterator<Long> iterator = queryIDs.iterator();
		for( int i=0; i<MAXSTOREITERATIONS; i++ ) {
			Long qid = iterator.next();
			store.startQuery(qid);
			System.out.println("STARTING QUERY " + qid);
			for( int j=0; j<MAXSTORERECOMMENDER; j++ ) { 
				ResultStoreProducer producer = 
					new ResultStoreProducer(store,qid,new Long(j),MAXSTORENROFTAGS,Math.round(Math.random()*1000)+1);
				producers.add(producer);
				producer.start();
			}
		}
		try {
			Thread.sleep(5000);
		} catch (InterruptedException ex) {
		}
		
		//
		// run tests
		//
		iterator = queryIDs.iterator();
		while( iterator.hasNext() ) {
			Long qid = iterator.next();
			store.stopQuery(qid);
			// all producers should have delivered results
			assertEquals(
					"Resultstore missed a recommender.",
					MAXSTORERECOMMENDER,
					store.getActiveRecommender(qid).size()
				  );
			// sum up the total number of recommended tags (from all producers in the query)
			Collection<SortedSet<RecommendedTag>> resultsForQuery = store.getResultForQuery(qid);
			int counter = 0;
			for( SortedSet<RecommendedTag> result : resultsForQuery )
				counter += result.size();
			assertEquals(
					"Resultstore missed a result.",
					MAXSTORERECOMMENDER*MAXSTORENROFTAGS,
					counter
				  );
			// try to add a result to a stopped query
			store.addResult(qid, new Long(42), new TreeSet<RecommendedTag>());
			assertEquals(
					"Resultstore added a result after query timed out.",
					MAXSTORERECOMMENDER,
					store.getActiveRecommender(qid).size()
				  );
		}
		
		iterator = queryIDs.iterator();
		for( int i=0; i<MAXSTOREITERATIONS; i++ ) {
			Long qid = iterator.next();
			store.releaseQuery(qid);
			// assure that a released query doesn't deliver tags
			assertNull(
					"Resultstore delivered result for released query.",
					store.getResultForQuery(qid)
				  );
			// assure that a released query is removed
			assertEquals(
					"Result store did not release query.",
					MAXSTOREITERATIONS-(i+1),
					store.getNrOfCachedQueries()
					);
		}
	}
	
	
	/**
	 * Test querying a lot of recommender in parallel 
	 * @throws Exception 
	 */
	@Test
	public void testMultiThreading() throws Exception {
		// create dummy recommenders
		List<TagRecommenderConnector> recos = new ArrayList<TagRecommenderConnector>(NROFRECOS);
		for( int i=0; i<NROFRECOS; i++ ) {
			DummyTagRecommender reco = new DummyTagRecommender();
			reco.setWait(MSTOWAIT);
			reco.setId(i);
			reco.initialize(null);
			reco.connect();
			recos.add(reco);
		}
		
		// create recommender counter which counts the number of
		// recommenders which delivered tags
		SelectCounter selector = new SelectCounter();
		selector.setDbLogic(dbLogic);
		
		// create multiplexer
		MultiplexingTagRecommender multi = new MultiplexingTagRecommender();
		multi.setDbLogic(dbLogic);
		multi.setResultSelector(selector);
		multi.setQueryTimeout(5*MSTOWAIT);
		
		// add dummy recommender
		multi.setDistRecommenders(recos);
		
		// initialize multiplexer
		multi.init();
		
		// query recommender
		Post<? extends Resource> post = createPost();
		multi.getRecommendedTags(post);
		
		// shut down
		for( TagRecommenderConnector reco : recos )
			reco.disconnect();
		
		// test
		assertEquals("Not all recommenders delivered results", NROFRECOS, selector.getRecoCounter());
	}
	
	//------------------------------------------------------------------------
	// private helpers
	//------------------------------------------------------------------------
	/**
	 * Create an mockup post
	 */
	private static Post<? extends Resource> createPost() {
		final Post<Resource> post = new Post<Resource>();
		final User user = new User();
		user.setName("foo");
		final Group group = new Group();
		group.setName("bar");
		final Tag tag = new Tag();
		tag.setName("foobar");
		post.setUser(user);
		post.getGroups().add(group);
		post.getTags().add(tag);
		post.setDate(new Date(System.currentTimeMillis()));
		final BibTex bibtex = new BibTex();
		bibtex.setTitle("foo and bar");
		bibtex.setIntraHash("abc");
		bibtex.setInterHash("abc");
		bibtex.setYear("2009");
		bibtex.setBibtexKey("test");
		bibtex.setEntrytype("twse");
		post.setResource(bibtex);
		post.setContentId(new Integer(0));
		post.addGroup("public");
		return post;
	}
}
