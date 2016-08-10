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
package org.bibsonomy.recommender.item.testutil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.recommender.item.content.ContentBasedItemRecommenderTest;
import org.bibsonomy.recommender.item.model.RecommendationUser;
import org.bibsonomy.recommender.item.service.RecommenderMainItemAccess;

public abstract class DummyMainItemAccess<R extends Resource> implements RecommenderMainItemAccess<R> {

	public static String[] CF_DUMMY_USERNAMES = {"cfusera", "cfuserb"};
	public static String[][] CF_DUMMY_USER_ITEMS = {{"recommender systems", "collaborative filtering"},
													{"evaluation trees", "grass green"}};
	private static final Random random = new Random();
	static {
		random.setSeed(12313234);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.item.service.RecommenderMainItemAccess#getMostActualItems(int, org.bibsonomy.recommender.item.model.RecommendationUser)
	 */
	@Override
	public List<Post<R>> getMostActualItems(int count, RecommendationUser entity) {
		final List<Post<R>> itemsForContentBasedFiltering = getItemsForContentBasedFiltering(count, entity);
		itemsForContentBasedFiltering.addAll(createRandomDummyPosts(count - itemsForContentBasedFiltering.size()));
		while (itemsForContentBasedFiltering.size() > count) {
			itemsForContentBasedFiltering.remove(itemsForContentBasedFiltering.size() - 1);
		}
		return itemsForContentBasedFiltering;
	}

	/**
	 * @param numberOfPosts
	 * @return
	 */
	private List<Post<R>> createRandomDummyPosts(int numberOfPosts) {
		final List<Post<R>> posts = new LinkedList<Post<R>>();
		for (int i = 0; i < numberOfPosts; i++) {
			posts.add(createDummyPost("Dummy-" + i));
		}
		return posts;
	}

	@Override
	public List<Post<R>> getItemsForContentBasedFiltering(int maxItemsToEvaluate, final RecommendationUser entity) {
		final List<Post<R>> items = new ArrayList<Post<R>>();
		for (int i = 0; i < CF_DUMMY_USERNAMES.length; i++) {
			for (int j = 0; j < CF_DUMMY_USER_ITEMS[i].length; j++) {
				Post<R> post = createDummyPost(CF_DUMMY_USER_ITEMS[i][j]);
				items.add(post);
			}
		}
		
		items.addAll(this.createRandomDummyPosts(3));
		
		return items;
	}
	
	private Post<R> createDummyPost(String title) {
		Post<R> post = new Post<R>();
		post.setContentId(Integer.valueOf(random.nextInt()));
		final R bookmark = this.createResource();
		bookmark.setTitle(title);
		post.setResource(bookmark);
		for(int i = 0 ; i < 5 ; i++) {
			post.addTag("tag" + i);
		}
		return post;
	}
	
	/**
	 * @return
	 */
	protected abstract R createResource();

	@Override
	public List<Post<? extends Resource>> getItemsForUser(int count, String username) {
		for (int i = 0; i < CF_DUMMY_USERNAMES.length; i++) {
			if (username.equals(CF_DUMMY_USERNAMES[i])) {
				final List<Post<? extends Resource>> items = new ArrayList<Post<? extends Resource>>();
				for (int j = 0; j < CF_DUMMY_USER_ITEMS[i].length; j++) {
					items.add(createDummyPost(CF_DUMMY_USER_ITEMS[i][j]));
				}
				return items;
			}
		}
		if (username.equals(ContentBasedItemRecommenderTest.DUMMY_CF_USER_NAME)) {
			final List<Post<? extends Resource>> items = new ArrayList<Post<? extends Resource>>();
			items.addAll(getUserPosts());
			return items;
		}
 		return Collections.emptyList();
	}

	private List<Post<R>> getUserPosts() {
		final List<Post<R>> items = new ArrayList<Post<R>>();
		for(int j = 0; j < ContentBasedItemRecommenderTest.TEST_USER_ITEMS.length; j++) {
			items.add(createDummyPost(ContentBasedItemRecommenderTest.TEST_USER_ITEMS[j]));
		}
		return items;
	}

	@Override
	public List<Post<R>> getTaggedItems(final int maxItemsToEvaluate, final Set<String> tags) {
		// do nothing
		return null;
	}
}
