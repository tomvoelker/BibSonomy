package org.bibsonomy.recommender.connector.testutil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.bibsonomy.recommender.connector.database.params.RecommendationBibTexParam;
import org.bibsonomy.recommender.connector.model.RecommendedPost;

import recommender.core.interfaces.database.RecommenderMainItemAccess;
import recommender.core.interfaces.model.ItemRecommendationEntity;
import recommender.core.interfaces.model.RecommendationItem;

public class DummyMainItemAccess implements RecommenderMainItemAccess{

	@Override
	public List<RecommendationItem> getMostActualItems(int count,
			ItemRecommendationEntity entity) {
		
		ArrayList<RecommendationItem> items = new ArrayList<RecommendationItem>();
		
		for(int i = 0; i < count; i++) {
			RecommendationBibTexParam param = new RecommendationBibTexParam();
			param.setId("testitem"+i);
			param.setTitle("testitem"+i);
			param.setOwnerName("foo.bar");
			items.add(param.getCorrespondingRecommendationItem());
		}
		
		return items;
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
			items.add(new RecommendedPost<BibTex>(post));
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
				items.add(new RecommendedPost<BibTex>(post));
			}
		}
		return items;
	}
	
}
