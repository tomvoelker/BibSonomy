/**
 * BibSonomy Recommendation - Tag and resource recommender.
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
package org.bibsonomy.recommender.tag.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.recommender.connector.testutil.RecommenderTestContext;
import org.bibsonomy.recommender.tag.model.RecommendedTag;
import org.bibsonomy.recommender.tag.testutil.DummyTagRecommender;
import org.bibsonomy.testutil.TestDatabaseLoader;
import org.junit.BeforeClass;
import org.junit.Test;

import recommender.core.database.DBLogic;
import recommender.core.database.params.RecQueryParam;
import recommender.impl.meta.ResultsFromFirstWeightedBySecondRecommender;
import recommender.impl.webservice.WebserviceRecommender;

/**
 * Test cases for recommender's DBAccess class, with old 
 * BibSonomy TagRecommender tables.
 * 
 * @author fei
 */
public class DBTagAccessTest {

	private static DBLogic<Post<? extends Resource>, RecommendedTag> dbLogic;
	
	@BeforeClass
	public static void setUp() {
		dbLogic = RecommenderTestContext.getBeanFactory().getBean("tagRecommenderLogic", DBLogConfigBibSonomy.class);
		final TestDatabaseLoader loader = new TestDatabaseLoader("recommender-db-schema.sql");
		loader.load("recommender-test.properties", "recommender.tag");
	}
	
	/**
	 * Test registering a new recommender
	 */
	@Test
	public void testAddQuery() {
		final Post<? extends Resource> post = createPost();
		final Timestamp ts = new Timestamp(System.currentTimeMillis());
		
		// store and retrieve query
		final Long qid = dbLogic.addQuery(post.getUser().getName(), ts, post, 1234);
		final RecQueryParam<?> retVal = dbLogic.getQuery(qid);
		
		final String queryUN = retVal.getUserName();
		assertEquals(post.getUser().getName(), queryUN);
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
	 * Test retrieving setting ids of registered recommenders by their qualified name or url
	 * @throws MalformedURLException 
	 */
	@Test
	public void testGetRecommenderSid() throws MalformedURLException {
		final ResultsFromFirstWeightedBySecondRecommender<Post<? extends Resource>, RecommendedTag> firstRecommender = new ResultsFromFirstWeightedBySecondRecommender<Post<? extends Resource>, RecommendedTag>();
		dbLogic.registerRecommender(firstRecommender);
		
		assertTrue(dbLogic.getRecommenderId(firstRecommender).longValue() > -1);
		
		final DummyTagRecommender notExistingRecommender = new DummyTagRecommender();
		assertEquals(Long.valueOf(-1), dbLogic.getRecommenderId(notExistingRecommender));
		
		final WebserviceRecommender<Post<? extends Resource>, RecommendedTag> webserviceRecommender = new WebserviceRecommender<Post<? extends Resource>, RecommendedTag>();
		webserviceRecommender.setAddress(new URL("http://example.com"));
		
		dbLogic.registerRecommender(webserviceRecommender);
		
		assertTrue(dbLogic.getRecommenderId(webserviceRecommender).longValue() > -1);
	}
	
	/**
	 * Test mapping post to recommendation
	 */
	@Test
	public void testGetQueryForPost() {
		/*
		 *  add query
		 */
		final Post<? extends Resource> post = createPost();
		final Timestamp ts = new Timestamp(System.currentTimeMillis());
		final String postID = String.valueOf(post.getContentId());
		
		// store and retrieve query
		final Long qid = dbLogic.addQuery(post.getUser().getName(), ts, post, 1234);
		final Long  id = dbLogic.getQueryForEntity(post.getUser().getName(), ts, postID);
		
		assertEquals(qid, id);
	}
	
	//------------------------------------------------------------------------
	// private helpers
	//------------------------------------------------------------------------
	/**
	 * Create an mockup post
	 */
	private static Post<? extends Resource> createPost() {
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
		
		return post;
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