package org.bibsonomy.recommender.item.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.bibsonomy.recommender.connector.testutil.RecommenderTestContext;
import org.bibsonomy.recommender.item.model.RecommendationUser;
import org.bibsonomy.recommender.item.model.RecommendedPost;
import org.bibsonomy.recommender.item.simple.DummyItemRecommender;
import org.bibsonomy.testutil.TestDatabaseLoader;
import org.junit.BeforeClass;
import org.junit.Test;

import recommender.core.database.DBLogic;
import recommender.core.database.params.RecQueryParam;
import recommender.core.util.RecommendationResultComparator;
import recommender.impl.webservice.WebserviceRecommender;

/**
 * This class tests the database logging and configuration logic for
 * the model implementation of RecommendedItems. 
 * 
 * @author lukas
 *
 */
public class DBItemAccessTest {
	private static DBLogic<RecommendationUser, RecommendedPost<BibTex>> dbLogic;
	
	@BeforeClass
	public static void setUp() {
		// bibtexRecommenderLogic is representational for all items in this case
		dbLogic = RecommenderTestContext.getBeanFactory().getBean("bibtexRecommenderLogic", DBLogConfigItemAccess.class);
		final TestDatabaseLoader loader = new TestDatabaseLoader("database/recommender_schema.sql");
		loader.load("recommender-test.properties", "recommender.item");
	}
	
	/**
	 * Test registering a new recommender
	 */
	@Test
	public void testAddQuery() {
		final RecommendationUser entity = this.createItemRecommendationEntity();
		final Timestamp ts = new Timestamp(System.currentTimeMillis());
		
		// store and retrieve query
		final Long qid = dbLogic.addQuery(entity.getUserName(), ts, entity, 1234);
		final RecQueryParam retVal = dbLogic.getQuery(qid);
		
		final String queryUN = retVal.getUserName();
		assertEquals(entity.getUserName(), queryUN);
	}
	
	/**
	 * Test adding selected results.
	 */
	@Test
	public void testAddSelectedItems()  {
		final Long qid = Long.valueOf(0);
		final Long rid = Long.valueOf(0);
		final int nr = 5;
		
		// create items
		final SortedSet<RecommendedPost<BibTex>> items = this.createRecommendedItems(nr);
		// store items
		final int count = dbLogic.storeRecommendation(qid, rid, items);
		// fetch items
		final List<RecommendedPost<BibTex>> result = dbLogic.getSelectedResults(Long.valueOf(0));
		
		// compare items
		final SortedSet<RecommendedPost<BibTex>> sort = new TreeSet<RecommendedPost<BibTex>>();
		assertEquals(nr, count);
		sort.addAll(result);
		final int i=0;
		for( final RecommendedPost<BibTex> item : sort ) {
			assertEquals(item.getTitle(), "testTitle" + (new Integer(i)).toString());
			assertEquals((1.0*i)/count, item.getScore(), 0.0);
			assertEquals(1.0/count, item.getConfidence(), 0.0);
		}
	}
	
	/**
	 * Test adding recommender response
	 */
	@Test
	public void testAddRecommenderResult() {
		final Long qid = Long.valueOf(0);
		final Long sid = Long.valueOf(0);
		final Long latency = Long.valueOf(0);
		
		final SortedSet<RecommendedPost<BibTex>> recommendations = new TreeSet<RecommendedPost<BibTex>>(new RecommendationResultComparator<RecommendedPost<BibTex>>());
		// usage of unix timestamp as unique id
		recommendations.add(this.createRecommendedItemWithId((int) (System.currentTimeMillis()/1000L)));
		
		final int count = dbLogic.addRecommendation(qid, sid, recommendations, latency);
		
		assertEquals(count, recommendations.size());
	}
	
	/**
	 * Test retrieving setting ids of registered recommenders by their qualified name or url
	 */
	@Test
	public void testGetRecommenderSid() throws MalformedURLException {
		final DummyItemRecommender<BibTex> dummyItemRecommender = new DummyItemRecommender<BibTex>();
		if (!dbLogic.isRecommenderRegistered(dummyItemRecommender)) {
			dbLogic.registerRecommender(dummyItemRecommender);
		}
		
		assertTrue(dbLogic.getRecommenderId(dummyItemRecommender).longValue() > -1L);
				
		final WebserviceRecommender<RecommendationUser, RecommendedPost<BibTex>> webserviceRecommender = new WebserviceRecommender<RecommendationUser, RecommendedPost<BibTex>>();
		webserviceRecommender.setAddress(new URL("http://example.com"));
		assertEquals(Long.valueOf(-1), dbLogic.getRecommenderId(webserviceRecommender));
		
		dbLogic.registerRecommender(webserviceRecommender);
		assertTrue(dbLogic.getRecommenderId(webserviceRecommender).longValue() > -1L);
		
		dbLogic.removeRecommender(webserviceRecommender);
		
		assertEquals(Long.valueOf(-1), dbLogic.getRecommenderId(webserviceRecommender));
	}
	
	/**
	 * Test mapping post to recommendation
	 */
	@Test
	public void testGetQueryForPost() {
		/*
		 *  add query
		 */
		final RecommendationUser post = this.createItemRecommendationEntity();
		final Timestamp ts = new Timestamp(System.currentTimeMillis());
		final String postID = ""+(int) Math.floor(Math.random() * Integer.MAX_VALUE);
		
		// store and retrieve query
		final Long qid = dbLogic.addQuery(post.getUserName(), ts, post, 1234);
		final Long id = dbLogic.getQueryForEntity(post.getUserName(), ts, post.getUserName());
		
		assertEquals(qid, id);
	}
	
	/**
	 * private helper for getting instance of {@link ItemRecommendationEntity}
	 * 
	 * @return an instance of {@link ItemRecommendationEntity} with username 'foo'
	 */
	private RecommendationUser createItemRecommendationEntity() {
		final RecommendationUser user = new RecommendationUser();
		user.setUserName("foo");
		return user;
	}
	
	/**
	 * private helper for getting a set of instances of {@link RecommendedItem}
	 * 
	 * @param count the count of items to create
	 * @return a set of items with size count
	 */
	private SortedSet<RecommendedPost<BibTex>> createRecommendedItems(final int count) {
		final SortedSet<RecommendedPost<BibTex>> items = new TreeSet<RecommendedPost<BibTex>>(new RecommendationResultComparator<RecommendedPost<BibTex>>());
		for(int i = 0; i < count; i++) {
			final RecommendedPost<BibTex> item = this.createRecommendedItemWithId(i);
			item.setScore(1.0/(i+1.0));
			item.setConfidence(1.0/(i+1.0));
			items.add(item);
		}
		return items;
	}
	
	/**
	 * private helper for getting an {@link RecommendedItem} instance with specified id
	 * 
	 * @param id the id of the item to create
	 * @return the item instance with given id
	 */
	private RecommendedPost<BibTex> createRecommendedItemWithId(final int id) {
		final Post<BibTex> post = new Post<BibTex>();
		final BibTex bibtex = new BibTex();
		bibtex.setTitle("testTitle"+id);
		post.setContentId(id);
		post.setTags(new HashSet<Tag>());
		post.setResource(bibtex);
		final RecommendedPost<BibTex> item = new RecommendedPost<BibTex>();
		item.setPost(post);
		return item;
	}
}
