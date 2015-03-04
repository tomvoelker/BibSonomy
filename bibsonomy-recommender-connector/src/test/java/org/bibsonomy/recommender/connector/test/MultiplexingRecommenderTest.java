/**
 * BibSonomy-Recommendation-Connector - Connector for the recommender framework for tag and resource recommendation
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.recommender.connector.database.DBLogConfigBibSonomy;
import org.bibsonomy.recommender.connector.database.ExtendedMainAccess;
import org.bibsonomy.recommender.connector.filter.PostPrivacyFilter;
import org.bibsonomy.recommender.connector.filter.UserPrivacyFilter;
import org.bibsonomy.recommender.connector.model.BibsonomyTagRendererFactoryWrapper;
import org.bibsonomy.recommender.connector.model.PostWrapper;
import org.bibsonomy.recommender.connector.model.UserWrapper;
import org.bibsonomy.recommender.connector.testutil.DummyMainItemAccess;
import org.bibsonomy.recommender.connector.testutil.RecommenderTestContext;
import org.bibsonomy.recommender.connector.testutil.SelectCounter;
import org.bibsonomy.rest.renderer.RendererFactory;
import org.bibsonomy.rest.renderer.UrlRenderer;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import recommender.core.Recommender;
import recommender.core.database.DBLogic;
import recommender.core.interfaces.RecommenderConnector;
import recommender.core.interfaces.model.ItemRecommendationEntity;
import recommender.core.interfaces.model.TagRecommendationEntity;
import recommender.impl.database.DBLogConfigItemAccess;
import recommender.impl.item.simple.DummyItemRecommender;
import recommender.impl.model.RecommendedItem;
import recommender.impl.model.RecommendedTag;
import recommender.impl.multiplexer.MultiplexingRecommender;
import recommender.impl.tags.simple.DummyTagRecommender;

/**
 * Tests the multithreading with BibSonomy's implementation
 * of the recommender's model classes.
 * 
 * @see recommender.impl.test.multiplexer.MultiplexingRecommenderTest
 * 
 * @author fei
 */
public class MultiplexingRecommenderTest {

	private static final int NROFRECOS = 10;
	private static final int MSTOWAIT = 1000;
	private static final int RECOMMENDATIONS_TO_CALCULATE = 4;
	
	private static DBLogic<TagRecommendationEntity, RecommendedTag> dbTagLogic;
	private static DBLogic<ItemRecommendationEntity, RecommendedItem> dbItemLogic;

	@BeforeClass
	public static void setUp() {
		dbTagLogic = RecommenderTestContext.getBeanFactory().getBean("tagRecommenderLogic", DBLogConfigBibSonomy.class);
		dbItemLogic = RecommenderTestContext.getBeanFactory().getBean("bibtexRecommenderLogic", DBLogConfigItemAccess.class);
	}

	/**
	 * Test querying a lot of recommender in parallel .
	 * This case tests the model implementation for tag recommendations.
	 * @throws Exception 
	 */
	@Test
	public void testTagMultiThreading() throws Exception {
		// create dummy recommenders
		final List<RecommenderConnector<TagRecommendationEntity, RecommendedTag>> recos = new ArrayList<RecommenderConnector<TagRecommendationEntity, RecommendedTag>>(NROFRECOS);
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
		final SelectCounter<TagRecommendationEntity, RecommendedTag> selector = new SelectCounter<TagRecommendationEntity, RecommendedTag>();
		selector.setDbLogic(dbTagLogic);

		// create multiplexer
		final MultiplexingRecommender<TagRecommendationEntity, RecommendedTag> multi = new MultiplexingRecommender<TagRecommendationEntity, RecommendedTag>();
		multi.setDbLogic(dbTagLogic);
		multi.setPrivacyFilter(new PostPrivacyFilter());
		multi.setRenderer(new BibsonomyTagRendererFactoryWrapper(new RendererFactory(new UrlRenderer("api/"))));
		multi.setResultSelector(selector);
		multi.setQueryTimeout(5*MSTOWAIT);

		// add dummy recommender
		multi.setDistRecommenders(recos);

		// initialize multiplexer
		multi.init();

		// query recommender
		final TagRecommendationEntity post = createPost();
		multi.getRecommendation(post);

		// shut down
		for( final RecommenderConnector<TagRecommendationEntity, RecommendedTag> reco : recos ) {
			reco.disconnect();
		}

		// test
		assertEquals("Not all recommenders delivered results", NROFRECOS, selector.getRecoCounter());
	}
	
	/**
	 * This case tests not the parallel querying but the model implementation
	 * for item recommendations.
	 * @throws Exception
	 */
	@Test
	public void testItemMultiThreading() throws Exception {
		
		final ExtendedMainAccess dbAccess = new DummyMainItemAccess();
		
		// create multiplexer
		MultiplexingRecommender<ItemRecommendationEntity, RecommendedItem> mux = new MultiplexingRecommender<ItemRecommendationEntity, RecommendedItem>();
		mux.setDbLogic(dbItemLogic);
		mux.setQueryTimeout(MSTOWAIT);
		
		List<Recommender<ItemRecommendationEntity, RecommendedItem>> locals = new ArrayList<Recommender<ItemRecommendationEntity, RecommendedItem>>();
		
		// create dummy recommender
		DummyItemRecommender reco = new DummyItemRecommender();
		reco.setDbAccess(dbAccess);
		reco.setDbLogic(dbItemLogic);
		locals.add(reco);
		
		UserPrivacyFilter filter = new UserPrivacyFilter();
		filter.setDbAccess(new DummyMainItemAccess());
		
		mux.setPrivacyFilter(filter);
		mux.setLocalRecommenders(locals);
		
		// dummy user
		User dummyUser = new User();
		dummyUser.setName("testuser");
		ItemRecommendationEntity entity = new UserWrapper(dummyUser);
		
		SelectCounter<ItemRecommendationEntity, RecommendedItem> selectionStrategy = new SelectCounter<ItemRecommendationEntity, RecommendedItem>();
		selectionStrategy.setDbLogic(dbItemLogic);
		mux.setResultSelector(selectionStrategy);
		
		mux.setNumberOfResultsToRecommend(RECOMMENDATIONS_TO_CALCULATE);
		
		mux.init();
		
		SortedSet<RecommendedItem> result = mux.getRecommendation(entity);
		
		// ensure result count is correct
		Assert.assertEquals(RECOMMENDATIONS_TO_CALCULATE, result.size());
		
		//ensure wrapping is successful
		for(RecommendedItem item : result) {
			Assert.assertNotNull(item.getId());
		}
	}
	
	//------------------------------------------------------------------------
	// private helpers
	//------------------------------------------------------------------------
	/**
	 * Create an mockup post
	 */
	private TagRecommendationEntity createPost() {
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
