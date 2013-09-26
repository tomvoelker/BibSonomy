package org.bibsonomy.recommender.connector.database;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.recommender.connector.model.RecommendationPost;

import recommender.core.interfaces.model.ItemRecommendationEntity;
import recommender.core.interfaces.model.RecommendationItem;

/**
 * 
 * This class implements the database access on the bibsonomy database
 *  for the recommendation library to recommend bookmark posts.
 * 
 * @author Lukas
 *
 */
public class RecommenderMainBookmarkAccessImpl extends AbstractRecommenderMainItemAccessImpl {
	
	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.database.RecommenderDBAccess#getMostActualItems(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<RecommendationItem> getMostActualItems(final int count, final ItemRecommendationEntity entity) {
		
		final DBSession mainSession = this.openMainSession();
		try {
			BookmarkParam bookmarkParam = new BookmarkParam();
			bookmarkParam.setGrouping(GroupingEntity.ALL);
			bookmarkParam.setGroupId(GroupID.PUBLIC.getId());
			bookmarkParam.setOffset(0);
			bookmarkParam.setLimit(count);
			bookmarkParam.setSimHash(HashID.INTRA_HASH);
			
			List<Post<Bookmark>> results = (List<Post<Bookmark>>) this.queryForList("getMostActualBookmark", bookmarkParam, mainSession);
			List<RecommendationItem> items = new ArrayList<RecommendationItem>(results.size());
			
			for(Post<Bookmark> bookmark : results) {
				RecommendationItem item =  new RecommendationPost(bookmark);
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
		final DBSession mainSession = this.openMainSession();
		try {
			final BookmarkParam bookmarkParam = new BookmarkParam();
			bookmarkParam.setRequestedUserName(username);
			bookmarkParam.setGroupId(GroupID.PUBLIC.getId());
			bookmarkParam.setOffset(0);
			bookmarkParam.setLimit(count);
			
			// only get reduced data, because it's enough for calculation
			List<Post<Bookmark>> results = (List<Post<Bookmark>>) this.queryForList("getReducedUserBookmark", bookmarkParam, mainSession);
			List<RecommendationItem> items = new ArrayList<RecommendationItem>(results.size());
			
			for(Post<Bookmark> bookmark : results) {
				RecommendationItem item =  new RecommendationPost(bookmark);
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
		final DBSession mainSession = this.openMainSession();
		try {
			final BookmarkParam bookmarkParam = new BookmarkParam();
			bookmarkParam.setGroupId(GroupID.PUBLIC.getId());
			bookmarkParam.setOffset(0);
			bookmarkParam.setLimit(count);
			
			List<RecommendationItem> items = new ArrayList<RecommendationItem>();
			for(String username : usernames) {
				bookmarkParam.setRequestedUserName(username);
				// only get reduced data, because it's enough for calculation
				List<Post<Bookmark>> results = (List<Post<Bookmark>>) this.queryForList("getReducedUserBookmark", bookmarkParam, mainSession);
				for(Post<Bookmark> bookmark : results) {
					RecommendationItem item =  new RecommendationPost(bookmark);
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
		final DBSession mainSession = this.openMainSession();
		try {
			final List<RecommendationItem> items = new ArrayList<RecommendationItem>();
			final BookmarkParam param = new BookmarkParam();
			param.setSimHash(HashID.INTRA_HASH);
			for(Integer id : ids) {
				param.setRequestedContentId(id);
				Post<Bookmark> bookmark = this.queryForObject("getBookmarkById", param, Post.class, mainSession);
				items.add(new RecommendationPost(bookmark));
			}
			return items;
			
		} finally {
			mainSession.close();
		}
	}
	
}
