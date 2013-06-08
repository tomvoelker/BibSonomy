package org.bibsonomy.recommender.connector.test;

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
import org.bibsonomy.common.enums.Privlevel;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.recommender.connector.filter.PostPrivacyFilter;
import org.bibsonomy.recommender.connector.model.PostWrapper;
import org.bibsonomy.recommender.connector.testutil.RecommenderTestContext;
import org.bibsonomy.recommender.connector.testutil.SelectCounter;
import org.junit.BeforeClass;
import org.junit.Test;

import recommender.core.Recommender;
import recommender.core.database.DBLogic;
import recommender.core.interfaces.database.RecommenderDBAccess;
import recommender.core.interfaces.filter.PrivacyFilter;
import recommender.core.interfaces.tags.TagRecommenderConnector;
import recommender.core.model.RecommendedTag;
import recommender.core.model.TagRecommendationEntity;
import recommender.impl.multiplexer.MultiplexingTagRecommender;
import recommender.impl.multiplexer.RecommendedTagResultManager;
import recommender.impl.tags.simple.DummyTagRecommender;
import recommender.impl.temp.copy.RecommendedTagComparator;

/**
 * @author fei
 * @version $Id$
 */
public class MultiplexingTagRecommenderTest {
	private static final Log log = LogFactory.getLog(MultiplexingTagRecommenderTest.class);

	private static final int NROFRECOS = 10;
	private static final int MSTOWAIT = 1000;
	private static final int MAXSTOREITERATIONS  = 100;
	private static final int MAXSTORERECOMMENDER = 50;
	private static final int MAXSTORENROFTAGS    = 5;

	private static DBLogic dbLogic;
	private static RecommenderDBAccess dbAccess;

	@BeforeClass
	public static void setUp() {
		dbLogic = RecommenderTestContext.getBeanFactory().getBean(DBLogic.class);
		dbAccess = RecommenderTestContext.getBeanFactory().getBean(RecommenderDBAccess.class);
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

		public ResultStoreProducer(final RecommendedTagResultManager store, final Long qid, final Long sid, final int nrOfTags, final long timeout) {
			this.store    = store;
			this.nrOfTags = nrOfTags;
			this.qid      = qid;
			this.sid      = sid;
			this.timeout  = timeout;
		}

		@Override
		public void run() {
			final SortedSet<RecommendedTag> result = 
				new TreeSet<RecommendedTag>(new RecommendedTagComparator());
			for( int i = 0; i < this.nrOfTags; i++ ) {
				result.add(new RecommendedTag("TAG_"+ i, (1.0*i)/(this.nrOfTags+1), 0.5));
			}

			try {
				Thread.sleep(this.timeout);
			} catch (final Exception ex) {
			}

			this.store.addResult(this.qid, this.sid, result);
		}
	}

	/**
	 * Test the concurrent result cache 
	 */
	@Test
	public void testResultStore() {
		// generate a set with different random ids 
		final Set<Long> queryIDs = new TreeSet<Long>();
		while( queryIDs.size() < MAXSTOREITERATIONS ) {
			queryIDs.add(Math.round(Math.random()*(Long.MAX_VALUE)));
		}

		// create store to test
		final RecommendedTagResultManager store = new RecommendedTagResultManager();

		//
		// spawn MAXSTORERECOMMENDER for each query id
		//
		final Collection<ResultStoreProducer> producers = new LinkedList<ResultStoreProducer>();
		Iterator<Long> iterator = queryIDs.iterator();
		for( int i=0; i<MAXSTOREITERATIONS; i++ ) {
			final Long qid = iterator.next();
			store.startQuery(qid);
			log.debug("STARTING QUERY " + qid);
			for( int j=0; j<MAXSTORERECOMMENDER; j++ ) { 
				final ResultStoreProducer producer = 
					new ResultStoreProducer(store, qid, Long.valueOf(j), MAXSTORENROFTAGS, Math.round(Math.random()*1000)+1);
				producers.add(producer);
				producer.start();
			}
		}
		try {
			Thread.sleep(5000);
		} catch (final InterruptedException ex) {
		}

		//
		// run tests
		//
		iterator = queryIDs.iterator();
		while( iterator.hasNext() ) {
			final Long qid = iterator.next();
			store.stopQuery(qid);
			// all producers should have delivered results
			assertEquals("Resultstore missed a recommender.", MAXSTORERECOMMENDER, store.getActiveRecommender(qid).size());
			// sum up the total number of recommended tags (from all producers in the query)
			final Collection<SortedSet<RecommendedTag>> resultsForQuery = store.getResultForQuery(qid);
			int counter = 0;
			for( final SortedSet<RecommendedTag> result : resultsForQuery ) {
				counter += result.size();
			}
			assertEquals("Resultstore missed a result.", MAXSTORERECOMMENDER * MAXSTORENROFTAGS, counter);
			// try to add a result to a stopped query
			store.addResult(qid, Long.valueOf(42), new TreeSet<RecommendedTag>());
			assertEquals("Resultstore added a result after query timed out.", MAXSTORERECOMMENDER, store.getActiveRecommender(qid).size());
		}

		iterator = queryIDs.iterator();
		for( int i = 0; i < MAXSTOREITERATIONS; i++ ) {
			final Long qid = iterator.next();
			store.releaseQuery(qid);
			// assure that a released query doesn't deliver tags
			assertNull("Resultstore delivered result for released query.", store.getResultForQuery(qid));
			// assure that a released query is removed
			assertEquals("Result store did not release query.", MAXSTOREITERATIONS-(i+1), store.getNrOfCachedQueries());
		}
	}


	/**
	 * Test querying a lot of recommender in parallel 
	 * @throws Exception 
	 */
	@Test
	public void testMultiThreading() throws Exception {
		// create dummy recommenders
		final List<TagRecommenderConnector> recos = new ArrayList<TagRecommenderConnector>(NROFRECOS);
		for( int i = 0; i < NROFRECOS; i++ ) {
			final DummyTagRecommender reco = new DummyTagRecommender();
			reco.setWait(MSTOWAIT);
			reco.setId(i);
			reco.initialize(null);
			reco.connect();
			recos.add(reco);
		}

		// create recommender counter which counts the number of
		// recommenders which delivered tags
		final SelectCounter selector = new SelectCounter();
		selector.setDbLogic(dbLogic);

		// create multiplexer
		final MultiplexingTagRecommender multi = new MultiplexingTagRecommender();
		multi.setDbLogic(dbLogic);
		multi.setDbAccess(dbAccess);
		multi.setPostPrivacyFilter(new PostPrivacyFilter());
		multi.setResultSelector(selector);
		multi.setQueryTimeout(5*MSTOWAIT);

		// add dummy recommender
		multi.setDistRecommenders(recos);

		// initialize multiplexer
		multi.init();

		final Recommender recommender = new Recommender();
		recommender.setTagRecommender(multi);
		// query recommender
		final TagRecommendationEntity post = createPost();
		recommender.getRecommendedTags(post);

		// shut down
		for( final TagRecommenderConnector reco : recos ) {
			reco.disconnect();
		}

		// test
		assertEquals("Not all recommenders delivered results", NROFRECOS, selector.getRecoCounter());
	}

	//------------------------------------------------------------------------
	// private helpers
	//------------------------------------------------------------------------
	/**
	 * Create an mockup post
	 */
	private static TagRecommendationEntity createPost() {
		final Post<BibTex> post = new Post<BibTex>();
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
		post.setContentId(0);
		post.addGroup("public");
		
		return new PostWrapper<BibTex>(post);
	}
}
