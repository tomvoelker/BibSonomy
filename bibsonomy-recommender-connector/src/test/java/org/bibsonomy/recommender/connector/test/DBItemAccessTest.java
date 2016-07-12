/**
 * BibSonomy-Recommendation-Connector - Connector for the recommender framework for tag and resource recommendation
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.recommender.connector.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
import org.bibsonomy.testutil.TestDatabaseLoader;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import recommender.core.database.DBLogic;
import recommender.core.database.params.RecQueryParam;
import recommender.core.database.params.RecSettingParam;
import recommender.core.database.params.SelectorSettingParam;
import recommender.core.interfaces.model.ItemRecommendationEntity;
import recommender.impl.database.DBLogConfigItemAccess;
import recommender.impl.model.RecommendedItem;
import recommender.impl.multiplexer.MultiplexingRecommender;

/**
 * This class tests the database logging and configuration logic for
 * the model implementation of RecommendedItems. 
 * 
 * @author lukas
 *
 */
public class DBItemAccessTest {
	private static DBLogic<ItemRecommendationEntity, RecommendedItem> dbLogic;
	protected static TestDatabaseLoader TEST_DB_LOADER = new TestDatabaseLoader("recommender_schema.sql");
	
	@BeforeClass
	public static void setUp() {
		// bibtexRecommenderLogic is representational for all items in this case
		dbLogic = RecommenderTestContext.getBeanFactory().getBean("bibtexRecommenderLogic", DBLogConfigItemAccess.class);
	}
	
	@Before
	public void initTestDatabase() {
		TEST_DB_LOADER.load("recommender-test.properties", "recommender.item");
	}
	
	/**
	 * Test registering a new recommender
	 */
	@Test
	public void testAddQuery() {
		final ItemRecommendationEntity entity = this.createItemRecommendationEntity();
		final Timestamp ts = new Timestamp(System.currentTimeMillis());
		
		// store and retrieve query
		final Long qid = dbLogic.addQuery(entity.getUserName(), ts, entity, MultiplexingRecommender.getUnknownEID(), 1234);
		final RecQueryParam retVal = dbLogic.getQuery(qid);
		
		final String queryUN = retVal.getUserName();
		assertEquals(entity.getUserName(), queryUN);
	}
	
	/**
	 * Test registering a new recommender
	 */
	@Test
	public void testAddNewRecommender()  {
		final Long qid = Long.valueOf(0);
		final String recInfo = "TestCase-non-Recommender";
		final String recMeta = "NON-NULL-META";
		final String recId = "mypackage.classname";
		// store and retrieve recommender informations
		Long sid;
		RecSettingParam retVal = null;
		sid = dbLogic.addRecommender(qid, recId, recInfo, recMeta.getBytes());
		retVal = dbLogic.getRecommender(sid);
		assertEquals(recId, retVal.getRecId());
		assertArrayEquals(recMeta.getBytes(), retVal.getRecMeta());
	}

	/**
	 * Test registering a new selector
	 */
	@Test
	public void testAddNewSelector()  {
		final Long qid = Long.valueOf(0);
		final String selectorInfo = "TestCase-non-Selector";
		final String selectorMeta = "NON-NULL-META";
		// store and retrieve recommender informations
		final Long sid = dbLogic.addResultSelector(qid, selectorInfo, selectorMeta.getBytes());
		final SelectorSettingParam retVal = dbLogic.getSelector(sid);
		assertEquals(selectorInfo, retVal.getInfo());
		assertArrayEquals(selectorMeta.getBytes(), retVal.getMeta());
	}	
	
	/**
	 * Test registering a new selector 
	 */
	@Test
	public void testAddNewSelector2()  {
		final Long qid = Long.valueOf(0);
		final String selectorInfo = "TestCase-non-Selector";
		final byte[] selectorMeta = null;
		// store and retrieve recommender informations
		Long sid = null;
		SelectorSettingParam retVal = null;
		sid = dbLogic.addResultSelector(qid, selectorInfo, selectorMeta);
		retVal = dbLogic.getSelector(sid);
		assertEquals(selectorInfo, retVal.getInfo());
		assertArrayEquals(null, retVal.getMeta());
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
	 * Test registering an already known recommender
	 */
	@Test
	public void testAddKnownRecommender() {
		final Long qid = Long.valueOf(0);
		final String recInfo = "TestCase-non-Recommender";
		final String recMeta = "NON-NULL-META";
		final String recId = "mypackage.classname";
		// store and retrieve recommender informations
		Long sid;
		RecSettingParam retVal = null;
		sid = dbLogic.addRecommender(qid, recId, recInfo, recMeta.getBytes());
		retVal = dbLogic.getRecommender(sid);
		assertEquals(recId, retVal.getRecId());
		assertArrayEquals(recMeta.getBytes(), retVal.getRecMeta());
		// store same recommender again and check if information stays valid
		sid = dbLogic.addRecommender(qid, recId, recInfo, recMeta.getBytes());
		retVal = dbLogic.getRecommender(sid);
		assertEquals(recId, retVal.getRecId());
		assertArrayEquals(recMeta.getBytes(), retVal.getRecMeta());
	}
	
	/**
	 * Test retrieving setting ids of registered recommenders by their qualified name or url
	 */
	@Test
	public void testGetRecommenderSid() {
		dbLogic.insertRecommenderSetting("recommender.impl.item.simple.DummyItemRecommender", 
				"foo", null);
		assertTrue(dbLogic.getSettingIdForLocalRecommender("recommender.impl.item.simple.DummyItemRecommender") > -1);
		assertTrue(dbLogic.getSettingIdForLocalRecommender("bar") == -1L);
		dbLogic.insertRecommenderSetting("http://example.com", "foo", "abc".getBytes());
		assertTrue(dbLogic.getSettingIdForDistantRecommender("http://example.com") > -1);
		assertTrue(dbLogic.getSettingIdForDistantRecommender("bar") == -1L);
		dbLogic.removeRecommender("http://example.com");
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
		final Long qid = dbLogic.addQuery(post.getUserName(), ts, post, postID, 1234);
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
