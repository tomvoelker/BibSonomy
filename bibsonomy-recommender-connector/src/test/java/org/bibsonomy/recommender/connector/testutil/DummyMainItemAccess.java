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
package org.bibsonomy.recommender.connector.testutil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.recommender.connector.database.ExtendedMainAccess;
import org.bibsonomy.recommender.connector.model.RecommendationPost;
import org.bibsonomy.util.ValidationUtils;

import recommender.core.interfaces.model.ItemRecommendationEntity;
import recommender.core.interfaces.model.RecommendationItem;

public class DummyMainItemAccess implements ExtendedMainAccess {

	private static int id_generator;
	
	@Override
	public List<RecommendationItem> getMostActualItems(final int count, final ItemRecommendationEntity entity) {
		
		return getItemsForUser(count, "foo");
	}

	@Override
	public List<String> getSimilarUsers(final int count, final ItemRecommendationEntity entity) {
		int counter = 0;
		List<String> user = new ArrayList<String>();
		for(int i = 0; i < count; i++) {
			user.add(counter++ + "user");
		}
		return user;
	}
	
	@Override
	public List<RecommendationItem> getItemsForUser(final int count, final String username) {
		List<RecommendationItem> items = new ArrayList<RecommendationItem>();
		int counter = 0;
		for(int i = 0; i < count; i++) {
			Post<BibTex> post = new Post<BibTex>();
			BibTex b = new BibTex();
			b.setTitle("item"+counter++ +username);
			b.setAbstract("");
			post.setDescription("");
			post.setContentId(id_generator++);
			post.setTags(new HashSet<Tag>());
			post.setResource(b);
			items.add(new RecommendationPost(post));
		}
		return items;
	}

	public List<RecommendationItem> getItemsForUsers(final int count, final List<String> usernames) {
		List<RecommendationItem> items = new ArrayList<RecommendationItem>();
		for(String user : usernames) {
			items.addAll(this.getItemsForUser(count, user));
		}
		return items;
	}

	@Override
	public List<RecommendationItem> getResourcesByIds(final List<Integer> ids) {
		
		final List<RecommendationItem> items = new ArrayList<RecommendationItem>();
		for(Integer id : ids) {
			final BibTex bib = new BibTex();
			bib.setTitle(""+id);
			final Post<BibTex> post = new Post<BibTex>();
			post.setContentId(id);
			post.setResource(bib);
			post.setUser(new User("no_name"));
			items.add(new RecommendationPost(post));
		}
		
		return items;
	}
	
	@Override
	public List<RecommendationItem> getAllItemsOfQueryingUser(final int count, final String username) {
		return this.getItemsForUser(count, username);
	}
	
	@Override
	public Long getUserIdByName(final String username) {
		return (long) username.hashCode();
	}

	@Override
	public Collection<RecommendationItem> getItemsForContentBasedFiltering(final int maxItemsToEvaluate, final ItemRecommendationEntity entity) {
		final List<String> similarUsers = this.getSimilarUsers(3, entity);
		if(ValidationUtils.present(similarUsers)) {
			return this.getItemsForUsers(maxItemsToEvaluate/similarUsers.size(), similarUsers);
		}
		return new ArrayList<RecommendationItem>();
	}
	
	@Override
	public List<RecommendationItem> getTaggedItems(final int maxItemsToEvaluate, final Set<String> tags) {
		// do nothing
		return null;
	}
	
	@Override
	public RecommendationItem getItemByTitle(final String title) {
		Post<Resource> post = new Post<Resource>();
		Resource res = new BibTex();
		res.setTitle(title);
		post.setContentId(id_generator++);
		post.setUser(new User("testuser"+id_generator++));
		post.setResource(res);
		return new RecommendationPost(post);
	}
	
	@Override
	public RecommendationItem getItemByUserIdWithHash(final String hash, final String userId) {
		Post<Resource> post = new Post<Resource>();
		Resource res = new BibTex();
		res.setTitle("dummy title");
		res.setIntraHash(hash);
		post.setContentId(id_generator++);
		post.setUser(new User(userId));
		post.setResource(res);
		return new RecommendationPost(post);
	}
}
