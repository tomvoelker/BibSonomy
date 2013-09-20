package org.bibsonomy.recommender.connector.testutil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.recommender.connector.database.ExtendedMainAccess;
import org.bibsonomy.recommender.connector.model.RecommendationPost;

import recommender.core.interfaces.model.ItemRecommendationEntity;
import recommender.core.interfaces.model.RecommendationItem;

public class DummyMainItemAccess implements ExtendedMainAccess {

	@Override
	public List<RecommendationItem> getMostActualItems(int count,
			ItemRecommendationEntity entity) {
		
		return getItemsForUser(count, "foo");
	}

	@Override
	public List<String> getSimilarUsers(int count,
			ItemRecommendationEntity entity) {
		int counter = 0;
		List<String> user = new ArrayList<String>();
		for(int i = 0; i < count; i++) {
			user.add(counter++ + "user");
		}
		return user;
	}
	
	@Override
	public List<RecommendationItem> getItemsForUser(int count, String username) {
		List<RecommendationItem> items = new ArrayList<RecommendationItem>();
		int counter = 0;
		for(int i = 0; i < count; i++) {
			Post<BibTex> post = new Post<BibTex>();
			BibTex b = new BibTex();
			b.setTitle("item"+counter++ +username);
			b.setAbstract("");
			post.setDescription("");
			post.setTags(new HashSet<Tag>());
			post.setResource(b);
			items.add(new RecommendationPost(post));
		}
		return items;
	}

	@Override
	public List<RecommendationItem> getItemsForUsers(int count,
			List<String> usernames) {
		int counter = 0;
		List<RecommendationItem> items = new ArrayList<RecommendationItem>();
		for(String user : usernames) {
			for(int i = 0; i < count; i++) {
				Post<BibTex> post = new Post<BibTex>();
				BibTex b = new BibTex();
				b.setTitle("item"+counter++ +user);
				b.setAbstract("");
				post.setDescription("");
				post.setTags(new HashSet<Tag>());
				post.setResource(b);
				items.add(new RecommendationPost(post));
			}
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
	public List<RecommendationItem> getAllItemsOfQueryingUser(int count,
			String username) {
		return null;
	}
	
	@Override
	public Long getUserIdByName(String username) {
		return (long) username.hashCode();
	}
}
