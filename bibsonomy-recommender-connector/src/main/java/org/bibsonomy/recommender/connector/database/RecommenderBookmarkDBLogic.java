package org.bibsonomy.recommender.connector.database;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.recommender.connector.database.params.ItemRecRequestParam;
import org.bibsonomy.recommender.connector.model.RecommendedPost;

import recommender.core.database.RecommenderDBSession;
import recommender.core.interfaces.model.ItemRecommendationEntity;
import recommender.core.interfaces.model.RecommendationItem;

public class RecommenderBookmarkDBLogic extends RecommenderMainItemAccessImpl {
	
	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.database.RecommenderDBAccess#getMostActualItems(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<RecommendationItem> getMostActualItems(final int count, final ItemRecommendationEntity entity) {
		
		final RecommenderDBSession mainSession = this.openMainSession();
		try {
			final ItemRecRequestParam param = new ItemRecRequestParam();
			param.setCount(count);
			param.setUserName(entity.getUserName());
			List<Post<Bookmark>> results = (List<Post<Bookmark>>) this.queryForList("getMostActualBookmark", param, mainSession);
			List<RecommendationItem> items = new ArrayList<RecommendationItem>(results.size());
			for(Post<Bookmark> bookmark : results) {
				RecommendationItem item =  new RecommendedPost<Bookmark>(bookmark);
				items.add(item);
			}
			
			return items;
		} finally {
			mainSession.close();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.database.RecommenderDBAccess#getItemsForUser(int, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<RecommendationItem> getItemsForUser(final int count, final String username) {
		final RecommenderDBSession mainSession = this.openMainSession();
		try {
			final ItemRecRequestParam param = new ItemRecRequestParam();
			param.setCount(count);
			param.setUserName(username);
			List<Post<Bookmark>> results = (List<Post<Bookmark>>) this.queryForList("getBookmarkForUser", param, mainSession);
			List<RecommendationItem> items = new ArrayList<RecommendationItem>(results.size());
			for(Post<Bookmark> bookmark : results) {
				RecommendationItem item =  new RecommendedPost<Bookmark>(bookmark);
				items.add(item);
			}
			
			return items;
		} finally {
			mainSession.close();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.database.RecommenderDBAccess#getItemsForUsers(int, java.util.List)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<RecommendationItem> getItemsForUsers(int count,
			List<String> usernames) {
		final RecommenderDBSession mainSession = this.openMainSession();
		try {
			final ItemRecRequestParam param = new ItemRecRequestParam();
			param.setCount(count);
			List<RecommendationItem> items = new ArrayList<RecommendationItem>();
			for(String username : usernames) {
				param.setUserName(username);
				List<Post<Bookmark>> results = (List<Post<Bookmark>>) this.queryForList("getBookmarkForUser", param, mainSession);
				for(Post<Bookmark> bookmark : results) {
					RecommendationItem item =  new RecommendedPost<Bookmark>(bookmark);
					items.add(item);
				}
			}
			return items;
		} finally {
			mainSession.close();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.recommender.connector.database.RecommenderDBLogic#getResourcesByIds(java.util.List)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<RecommendationItem> getResourcesByIds(final List<Integer> ids) {
		final RecommenderDBSession mainSession = this.openMainSession();
		try {
			List<RecommendationItem> items = new ArrayList<RecommendationItem>();
			for(Integer id : ids) {
				Post<Bookmark> bookmark = this.queryForObject("getBookmarkById", id, Post.class, mainSession);
				items.add(new RecommendedPost<Bookmark>(bookmark));
			}
			
			return items;
		} finally {
			mainSession.close();
		}
	}
	
}
