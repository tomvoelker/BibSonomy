package org.bibsonomy.recommender.item.db;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.database.common.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.DBSessionFactory;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.recommender.item.db.params.ItemRecRequestParam;
import org.bibsonomy.recommender.item.model.RecommendationUser;
import org.bibsonomy.recommender.item.model.RecommendedPost;
import org.bibsonomy.recommender.item.service.ExtendedMainAccess;

/**
 * Implementation of the general methods which are equal for publication and bookmark recommendations.
 * 
 * @author lukas
 * @param <T> 
 *
 */
public abstract class AbstractRecommenderMainItemAccessImpl<T extends Resource> extends AbstractDatabaseManager implements ExtendedMainAccess<T> {
	
	private static final int RETRIEVE_USERS_PER_TAG = 6;
	private static final int USE_USERS_PER_TAG = 2;
	private static final int TAGS_TO_EVALUATE = 2;
	
	protected static final int USERS_TO_EVALUATE = 3;
	
	private DBSessionFactory mainFactory;
	
	protected DBSession openMainSession() {
		return this.mainFactory.getDatabaseSession();
	}
	
	public void setMainFactory(DBSessionFactory mainFactory) {
		this.mainFactory = mainFactory;
	}
	
	protected List<RecommendedPost<T>> convertToRecommendedPost(final List<Post<T>> posts) {
		final List<RecommendedPost<T>> recommendedPosts = new LinkedList<RecommendedPost<T>>();
		
		for (Post<T> post : posts) {
			final RecommendedPost<T> recommendedPost = new RecommendedPost<T>();
			recommendedPost.setPost(post);
			recommendedPosts.add(recommendedPost);
		}
		
		return recommendedPosts;
	}
	
	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.database.RecommenderMainItemAccess#getSimilarUsers(int, recommender.core.interfaces.model.ItemRecommendationEntity)
	 */
	@Override
	public List<String> getSimilarUsers(final int count, final RecommendationUser entity) {
		final DBSession mainSession = this.openMainSession();
		try {
			final ItemRecRequestParam param = new ItemRecRequestParam();
			param.setUserName(entity.getUserName());
			param.setCount(count);
			
			List<String> usernames = this.queryForList("getSimilarUsersByFolkrank", param, String.class, mainSession);
			
			// if folkrank calculated users were present use those
			if (usernames.size() == count) {
				return usernames;
			}
			
			// try to get similar users per cosine similarity
			usernames = this.queryForList("getSimilarUsersByCosineSimilarity", param, String.class, mainSession);
			
			// TODO: fill up folkrank users with the new users?
			if(usernames.size() == count) {
				return usernames;
			}
			
			final int tagsToRetrieve = 100;
			param.setCount(tagsToRetrieve);
			// in case of folkrank did not give enough information select similar users by a more simple strategy
			List<String> mostImportantUserTags = this.queryForList("getMostImportantTagsOfUser", param, String.class, mainSession);
			Iterator<String> it = mostImportantUserTags.iterator();
			
			// take not the top tags because they might be not meaningful
			int counter = 0;
			while(it.hasNext()) {
				it.next();
				if(!(mostImportantUserTags.size()/2 + TAGS_TO_EVALUATE > counter && mostImportantUserTags.size()/2 <= counter)) {
					it.remove();
				}
				counter++;
			}
			
			final List<String> usernamesSimple = new ArrayList<String>();
			
			// get all users which used at least one of the requesting user's important tags
			param.setCount(RETRIEVE_USERS_PER_TAG);
			List<String> tempnames = new ArrayList<String>();
			for(String tagname : mostImportantUserTags) {
				param.setTag(tagname);
				tempnames = this.queryForList("getSimilarUsersByEqualTags", param, String.class, mainSession);
				counter = 0;
				for(String username : tempnames) {
					// prevent from evaluating too much users
					if(usernamesSimple.size() >= count) {
						break;
					}
					// take not the top users because they might spammed this tag
					if (tempnames.size()/2 + USE_USERS_PER_TAG > counter && tempnames.size()/2 <= counter) {
						if(!usernamesSimple.contains(username)) {
							usernamesSimple.add(username);
						}
					}
					counter++;
				}
				// prevent from evaluating too much users
				if(usernamesSimple.size() >= count) {
					break;
				}
			}
			
			if (usernamesSimple.size() > usernames.size()) {
				return usernamesSimple;
			}
			
			return usernames;
			
		} finally {
			mainSession.close();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.recommender.connector.database.ExtendedMainAccess#getAllItemsOfQueryingUser(int, java.lang.String)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Post<? extends Resource>> getAllItemsOfQueryingUser(final int count, final String username) {
		final List<Post<? extends Resource>> posts = new LinkedList<Post<? extends Resource>>();
		final DBSession mainSession = this.openMainSession();
		try {
			// get bookmarks of user
			final BookmarkParam bookmarkParam = new BookmarkParam();
			bookmarkParam.setRequestedUserName(username);
			bookmarkParam.setGroupId(GroupID.PUBLIC.getId());
			bookmarkParam.setOffset(0);
			bookmarkParam.setLimit(count);
			
			List<Post<Bookmark>> bookmarkResults = (List<Post<Bookmark>>) this.queryForList("getReducedUserBookmark", bookmarkParam, mainSession);
			posts.addAll(bookmarkResults);
			
			//get publications of user
			final BibTexParam bibtexParam = new BibTexParam();
			bibtexParam.setRequestedUserName(username);
			bibtexParam.setGroupId(GroupID.PUBLIC.getId());
			bibtexParam.setOffset(0);
			bibtexParam.setLimit(count);
			
			List<Post<BibTex>> bibtexResults = (List<Post<BibTex>>) this.queryForList("getReducedUserBibTex", bibtexParam, mainSession);
			posts.addAll(bibtexResults);
			
			return posts;
		} finally {
			mainSession.close();
		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.recommender.connector.database.ExtendedMainAccess#getUserIdByName(java.lang.String)
	 */
	@Override
	public Long getUserIdByName(final String username) {
		final DBSession mainSession = this.openMainSession();
		try {
			return this.queryForObject("getUserIdByName", username, Long.class, mainSession);
		} finally {
			mainSession.close();
		}
	}
}
