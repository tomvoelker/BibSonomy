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
package org.bibsonomy.recommender.connector.item.content;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.recommender.connector.content.AdaptedContentBasedItemRecommender;
import org.bibsonomy.recommender.connector.model.RecommendationPost;
import org.bibsonomy.recommender.connector.model.UserWrapper;
import org.bibsonomy.recommender.connector.testutil.DummyMainItemAccess;
import org.bibsonomy.recommender.connector.testutil.RecommenderTestContext;
import org.junit.BeforeClass;
import org.junit.Test;

import recommender.core.database.DBLogic;
import recommender.core.interfaces.database.RecommenderMainItemAccess;
import recommender.core.interfaces.model.ItemRecommendationEntity;
import recommender.core.interfaces.model.RecommendationItem;
import recommender.impl.database.DBLogConfigItemAccess;
import recommender.impl.item.content.ContentBasedItemRecommender;
import recommender.impl.model.RecommendedItem;

/**
 * This class tests the {@link AdaptedContentBasedItemRecommender} extension of
 * the library's version {@link ContentBasedItemRecommender}.
 * It checks whether additional bibtex and bookmark information is used.
 * 
 * @author lukas
 *
 */
public class ContentBasedItemRecommenderTest {

	private static DBLogic<ItemRecommendationEntity, RecommendedItem> bibtexDBLogic;
	
	private static final int RECOMMENDATIONS_TO_CALCULATE = 4;

	private static final String REQUESTING_USER_NAME = "requestUser";
	private static final String[] USER_NAMES = {"cfUser1", "cfUser2"}; 
	private static final String WINNER_TITLE = "winner title";
	
	private static int id_generator = 0;
	
	@BeforeClass
	public static void setUp() {
		bibtexDBLogic = RecommenderTestContext.getBeanFactory().getBean("bibtexRecommenderLogic", DBLogConfigItemAccess.class);
	}
	
	/**
	 * Checks the correct count of results and the handling of Bookmark and
	 * BibTex resources.
	 */
	@Test
	public void testAdaptedContentBasedItemRecommender() {
		
		RecommenderMainItemAccess dbAccess = new DummyMainItemAccess();
		
		AdaptedContentBasedItemRecommender reco = new AdaptedContentBasedItemRecommender();
		reco.setDbAccess(dbAccess);
		reco.setDbLogic(bibtexDBLogic);
		reco.setNumberOfItemsToRecommend(RECOMMENDATIONS_TO_CALCULATE);
		
		User u = new User(REQUESTING_USER_NAME);
		
		SortedSet<RecommendedItem> recommendations = reco.getRecommendation(new UserWrapper(u));
		
		// checks if the count of items correct
		assertEquals(RECOMMENDATIONS_TO_CALCULATE, recommendations.size());
		
		// new dbAccess to make the database results non random
		dbAccess = new DummyCollaborativeMainAccess();
		reco.setDbAccess(dbAccess);
		
		recommendations = reco.getRecommendation(new UserWrapper(u));
		
		// this makes sure, for requesting user his bibtex and bookmark resources
		// are used for getting his vocabulary
		// also it ensures, that abstracts and descriptions get used
		assertEquals(WINNER_TITLE, recommendations.first().getTitle());
	}
	
	/**
	 * helper method for creation of recommendation items
	 * 
	 * @return a list with fix specified attributes like given below
	 */
	private List<Post<? extends Resource>>createItemsForCfUsers() {
		final List<Post<? extends Resource>> posts = new ArrayList<Post<? extends Resource>>();
		posts.add(this.createBibTexPost(WINNER_TITLE, "recommender systems", "empty descr", USER_NAMES[0]));
		posts.add(this.createBibTexPost("failed", "recommender systems", "unknown description", USER_NAMES[1]));
		return posts;
	}
	
	/**
	 * Creates a bibtex Post
	 * 
	 * @param title the post's title
	 * @param abstractString the bibtex's abstract
	 * @param description the post's description
	 * @param username the username of the owner
	 * @return an instance of a bibtex post
	 */
	private Post<BibTex> createBibTexPost(final String title, final String abstractString, final String description, final String username) {
		final Post<BibTex> post = new Post<BibTex>();
		final BibTex bibtex = new BibTex();
		bibtex.setTitle(title);
		bibtex.setAbstract(abstractString);
		post.setDescription(description);
		post.setContentId(id_generator);
		id_generator++;
		post.setResource(bibtex);
		post.setUser(new User(username));
		return post;
	}
	
	/**
	 * Creates a bookmark post
	 * 
	 * @param title the post's title
	 * @param description the post's description
	 * @param username the username of the owder
	 * @return an instance of a bookmark post
	 */
	private Post<Bookmark> createBookmarkPost(final String title, final String description, final String username) {
		final Post<Bookmark> post = new Post<Bookmark>();
		final Bookmark bookmark = new Bookmark();
		bookmark.setTitle(title);
		post.setDescription(description);
		post.setContentId(id_generator);
		id_generator++;
		post.setResource(bookmark);
		post.setUser(new User(username));
		return post;
	}
	
	/**
	 * Extended dummy database implementation to return non random values.
	 * @author lukas
	 */
	private class DummyCollaborativeMainAccess extends DummyMainItemAccess {
		/*
		 * (non-Javadoc)
		 * @see org.bibsonomy.recommender.connector.testutil.DummyMainItemAccess#getAllItemsOfQueryingUser(int, java.lang.String)
		 */
		@Override
		public List<RecommendationItem> getAllItemsOfQueryingUser(int count,
				String username) {
			final List<RecommendationItem> items = new ArrayList<RecommendationItem>();
			final Post<BibTex> bibtexPost = createBibTexPost("request bibtex", "recommender systems", "empty descr", REQUESTING_USER_NAME);
			final Post<Bookmark> bookmarkPost = createBookmarkPost("request bookmark", "empty descr", REQUESTING_USER_NAME);
			items.add(new RecommendationPost(bibtexPost));
			items.add(new RecommendationPost(bookmarkPost));
			return items;
		}
		/*
		 * (non-Javadoc)
		 * @see org.bibsonomy.recommender.connector.testutil.DummyMainItemAccess#getItemsForUsers(int, java.util.List)
		 */
		@Override
		public List<RecommendationItem> getItemsForUsers(int count,
				List<String> usernames) {
			final List<RecommendationItem> items = new ArrayList<RecommendationItem>();
			for(Post<? extends Resource> post : createItemsForCfUsers()) {
				items.add(new RecommendationPost(post));
			}
			return items;
		}
		
		/*
		 * (non-Javadoc)
		 * @see org.bibsonomy.recommender.connector.testutil.DummyMainItemAccess#getResourcesByIds(java.util.List)
		 */
		@Override
		public List<RecommendationItem> getResourcesByIds(List<Integer> ids) {
			// no new items are returned to prevent the similar dummy items to be overwritten
			return new ArrayList<RecommendationItem>();
		}
	}
	
}
