package org.bibsonomy.recommender;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.recommender.tags.TagRecommenderConnector;
import org.bibsonomy.recommender.tags.database.DBAccess;
import org.bibsonomy.recommender.tags.database.DBLogic;
import org.bibsonomy.recommender.tags.multiplexer.MultiplexingTagRecommender;
import org.bibsonomy.recommender.tags.simple.DummyTagRecommender;
import org.bibsonomy.recommender.testutil.JNDITestDatabaseBinder;
import org.bibsonomy.recommender.testutil.SelectCounter;
import org.bibsonomy.services.recommender.TagRecommender;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author fei
 * @version $Id$
 */
public class MultiplexingTagRecommenderTest {
	private static final Logger log = Logger.getLogger(MultiplexingTagRecommenderTest.class);
	private DBLogic dbLogic;
	private static final int NROFRECOS = 100;
	private static final int MSTOWAIT = 1000;

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
	
	/**
	 * Test querying a lot of recommender in parallel 
	 * @throws Exception 
	 */
	@Test
	@Ignore
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
