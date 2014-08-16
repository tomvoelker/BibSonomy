package org.bibsonomy.recommender.connector.test;

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
import org.bibsonomy.model.User;
import org.bibsonomy.recommender.connector.model.RecommendationPost;
import org.bibsonomy.recommender.connector.model.UserWrapper;
import org.bibsonomy.recommender.connector.testutil.RecommenderTestContext;
import org.bibsonomy.recommender.item.db.DBLogConfigItemAccess;
import org.junit.BeforeClass;
import org.junit.Test;

import recommender.core.database.DBLogic;
import recommender.core.database.params.RecQueryParam;
import recommender.core.interfaces.model.ItemRecommendationEntity;
import recommender.impl.item.simple.DummyItemRecommender;
import recommender.impl.model.RecommendedItem;
import recommender.impl.webservice.WebserviceRecommender;

/**
 * This class tests the database logging and configuration logic for
 * the model implementation of RecommendedItems. 
 * 
 * @author lukas
 *
 */
public class DBItemAccessTest {
	private static DBLogic<ItemRecommendationEntity, RecommendedItem> dbLogic;
	
	@BeforeClass
	public static void setUp() {
		// bibtexRecommenderLogic is representational for all items in this case
		dbLogic = RecommenderTestContext.getBeanFactory().getBean("bibtexRecommenderLogic", DBLogConfigItemAccess.class);
	}
	
	/**
	 * Test registering a new recommender
	 */
	@Test
	public void testAddQuery() {
		final ItemRecommendationEntity entity = this.createItemRecommendationEntity();
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
		final SortedSet<RecommendedItem> items = this.createRecommendedItems(nr);
		// store items
		final int count = dbLogic.storeRecommendation(qid, rid, items);
		// fetch items
		final List<RecommendedItem> result = dbLogic.getSelectedResults(Long.valueOf(0));
		
		// compare items
		final SortedSet<RecommendedItem> sort = new TreeSet<RecommendedItem>();
		assertEquals(nr, count);
		sort.addAll(result);
		final int i=0;
		for( final RecommendedItem item : sort ) {
			assertEquals(item.getTitle(), "testTitle" + (new Integer(i)).toString());
			assertEquals((1.0*i)/count, item.getScore(), 0.0);
			assertEquals(1.0/count, item.getConfidence(), 0.0);
		}
	}
	
	/**
	 * Test adding recommender response
	 */
	@Test
	public void testAddRecommenderResult()  {
		final Long qid = Long.valueOf(0);
		final Long sid = Long.valueOf(0);
		final Long latency = Long.valueOf(0);
		
		final SortedSet<RecommendedItem> recommendations = new TreeSet<RecommendedItem>();
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
		final DummyItemRecommender dummyItemRecommender = new DummyItemRecommender();
		dbLogic.registerRecommender(dummyItemRecommender);
		
		assertTrue(dbLogic.getRecommenderId(dummyItemRecommender).longValue() > -1L);
		
		final String secondRecommenderId = "http://example.com";
		
		final WebserviceRecommender<ItemRecommendationEntity, RecommendedItem> webserviceRecommender = new WebserviceRecommender<ItemRecommendationEntity, RecommendedItem>();
		webserviceRecommender.setAddress(new URL("http://example.com"));
		
		assertTrue(dbLogic.getRecommenderId(webserviceRecommender).longValue() > -1L);
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
		final ItemRecommendationEntity post = this.createItemRecommendationEntity();
		final Timestamp ts = new Timestamp(System.currentTimeMillis());
		final String postID = ""+(int)Math.floor(Math.random()*Integer.MAX_VALUE);
		
		// store and retrieve query
		final Long qid = dbLogic.addQuery(post.getUserName(), ts, post, 1234);
		final Long  id = dbLogic.getQueryForEntity(post.getUserName(), ts, postID);
		
		assertEquals(qid, id);
	}
	
	/**
	 * private helper for getting instance of {@link ItemRecommendationEntity}
	 * 
	 * @return an instance of {@link ItemRecommendationEntity} with username 'foo'
	 */
	private ItemRecommendationEntity createItemRecommendationEntity() {
		final User user = new User("foo");
		return new UserWrapper(user);
	}
	
	/**
	 * private helper for getting a set of instances of {@link RecommendedItem}
	 * 
	 * @param count the count of items to create
	 * @return a set of items with size count
	 */
	private SortedSet<RecommendedItem> createRecommendedItems(final int count) {
		final SortedSet<RecommendedItem> items = new TreeSet<RecommendedItem>();
		for(int i = 0; i < count; i++) {
			final RecommendedItem item = this.createRecommendedItemWithId(i);
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
	private RecommendedItem createRecommendedItemWithId(final int id) {
		final Post<BibTex> post = new Post<BibTex>();
		final BibTex bibtex = new BibTex();
		bibtex.setTitle("testTitle"+id);
		post.setContentId(id);
		post.setTags(new HashSet<Tag>());
		post.setResource(bibtex);
		final RecommendedItem item = new RecommendedItem(new RecommendationPost(post));
		return item;
	}
}
