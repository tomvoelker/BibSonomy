/**
 * BibSonomy-Recommendation-Connector - Connector for the recommender framework for tag and resource recommendation
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.recommender.connector.database.DBLogConfigBibSonomy;
import org.bibsonomy.recommender.connector.model.PostWrapper;
import org.bibsonomy.recommender.connector.testutil.RecommenderTestContext;
import org.bibsonomy.testutil.TestDatabaseLoader;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import recommender.core.database.DBLogic;
import recommender.core.database.params.RecQueryParam;
import recommender.core.database.params.RecSettingParam;
import recommender.core.database.params.SelectorSettingParam;
import recommender.core.interfaces.model.TagRecommendationEntity;
import recommender.impl.model.RecommendedTag;
import recommender.impl.multiplexer.MultiplexingRecommender;

/**
 * Test cases for recommender's DBAccess class, with old 
 * BibSonomy TagRecommender tables.
 * 
 * @author fei
 */
public class DBTagAccessTest {
	private static DBLogic<TagRecommendationEntity, RecommendedTag> dbLogic;
	protected static TestDatabaseLoader TEST_DB_LOADER = new TestDatabaseLoader("recommender-db-schema.sql");
	
	@BeforeClass
	public static void setUp() {
		dbLogic = RecommenderTestContext.getBeanFactory().getBean("tagRecommenderLogic", DBLogConfigBibSonomy.class);
	}
	
	@Before
	public void initTestDatabase() {
		TEST_DB_LOADER.load("recommender-test.properties", "recommender.tag");
	}
	
	/**
	 * Test registering a new recommender
	 */
	@Test
	public void testAddQuery() {
		final TagRecommendationEntity post = createPost();
		final Timestamp ts = new Timestamp(System.currentTimeMillis());
		
		// store and retrieve query
		final Long qid = dbLogic.addQuery(post.getUser().getName(), ts, post, MultiplexingRecommender.getUnknownEID(), 1234);
		final RecQueryParam retVal = dbLogic.getQuery(qid);
		
		final String queryUN = retVal.getUserName();
		assertEquals(post.getUser().getName(), queryUN);
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
	 * Test adding selected tags.
	 */
	@Test
	public void testAddSelectedTags()  {
		final Long qid = Long.valueOf(0);
		final Long rid = Long.valueOf(0);
		final int nr = 5;
		
		// create tags
		final SortedSet<RecommendedTag> tags = this.createRecommendedTags(nr);
		// store tags
		final int count = dbLogic.storeRecommendation(qid, rid, tags);
		// fetch tags
		final List<RecommendedTag> result = dbLogic.getSelectedResults(Long.valueOf(0));
		
		// compare tags
		final SortedSet<RecommendedTag> sort = new TreeSet<RecommendedTag>();
		assertEquals(nr, count);
		sort.addAll(result);
		final int i=0;
		for( final RecommendedTag tag : sort ) {
			assertEquals(tag.getName(), "Tag" + (new Integer(i)).toString());
			assertEquals((1.0*i)/count, tag.getScore(), 0.0);
			assertEquals(1.0/count, tag.getConfidence(), 0.0);
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
		
		final SortedSet<RecommendedTag> recommendations = new TreeSet<RecommendedTag>();
		recommendations.add(new RecommendedTag((new Date() + "tag"+System.currentTimeMillis()).replaceAll(" ", ""), 0.0, 0.0));
		
		final int count = dbLogic.addRecommendation(qid, sid, recommendations, latency.longValue());
		
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
		dbLogic.insertRecommenderSetting("recommender.impl.tags.meta.TagsFromFirstWeightedBySecondTagRecommender", 
				"foo", null);
		assertTrue(dbLogic.getSettingIdForLocalRecommender("recommender.impl.tags.meta.TagsFromFirstWeightedBySecondTagRecommender") > -1);
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
		final TagRecommendationEntity post = createPost();
		final Timestamp ts = new Timestamp(System.currentTimeMillis());
		final String postID = ""+(int)Math.floor(Math.random()*Integer.MAX_VALUE);
		
		// store and retrieve query
		final Long qid = dbLogic.addQuery(post.getUser().getName(), ts, post, postID, 1234);
		final Long  id = dbLogic.getQueryForEntity(post.getUser().getName(), ts, postID);
		
		assertEquals(qid, id);
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
	
	/**
	 * Create list of recommended tags.
	 * 
	 * @return
	 */
	protected SortedSet<RecommendedTag> createRecommendedTags(final int nr) {
		final TreeSet<RecommendedTag> extracted = new TreeSet<RecommendedTag>();

		// create informative recommendation:
		for (int i = 0; i < nr; i++) {
			final double score = (1.0 * i) / nr;
			final double confidence = 1.0 / nr;
			final String re = "Tag" + String.valueOf(i);
			extracted.add(new RecommendedTag(re, score, confidence));
		}
		return extracted;
	}		
}
