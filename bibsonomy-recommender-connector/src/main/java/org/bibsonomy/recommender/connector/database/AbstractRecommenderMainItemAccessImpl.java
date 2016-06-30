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
package org.bibsonomy.recommender.connector.database;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.database.common.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.DBSessionFactory;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.recommender.connector.database.params.ItemRecRequestParam;
import org.bibsonomy.recommender.connector.model.RecommendationPost;

import recommender.core.interfaces.model.ItemRecommendationEntity;
import recommender.core.interfaces.model.RecommendationItem;

/**
 * Implementation of the general methods which are equal for publication and bookmark recommendations.
 * 
 * @author lukas
 *
 */
public abstract class AbstractRecommenderMainItemAccessImpl extends AbstractDatabaseManager implements ExtendedMainAccess {
	
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
	
	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.database.RecommenderMainItemAccess#getMostActualItems(int)
	 */
	@Override
	public abstract List<RecommendationItem> getMostActualItems(final int count, final ItemRecommendationEntity entity);
	
	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.database.RecommenderMainItemAccess#getSimilarUsers(int, recommender.core.interfaces.model.ItemRecommendationEntity)
	 */
	@Override
	public List<String> getSimilarUsers(final int count, final ItemRecommendationEntity entity) {
		
		final DBSession mainSession = this.openMainSession();
		try {
			final ItemRecRequestParam param = new ItemRecRequestParam();
			param.setUserName(entity.getUserName());
			param.setCount(count);
			
			List<String> usernames = this.queryForList("getSimilarUsersByFolkrank", param, String.class, mainSession);
			
			// if folkrank calculated users were present use those
			if(usernames.size() == count) {
				return usernames;
			}
			
			// try to get similar users per cosine similarity
			usernames = this.queryForList("getSimilarUsersByCosineSimilarity", param, String.class, mainSession);
			
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
					if(tempnames.size()/2 + USE_USERS_PER_TAG > counter && tempnames.size()/2 <= counter) {
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
			
			if(usernamesSimple.size() > usernames.size()) {
				return usernamesSimple;
			}
			
			return usernames;
			
		} finally {
			mainSession.close();
		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.database.RecommenderMainItemAccess#getItemsForUser(int, java.lang.String)
	 */
	@Override
	public abstract List<RecommendationItem> getItemsForUser(int count, String username);
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.recommender.connector.database.ExtendedMainAccess#getResourcesByIds(java.util.List)
	 */
	@Override
	public abstract List<RecommendationItem> getResourcesByIds(final List<Integer> ids);
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.recommender.connector.database.ExtendedMainAccess#getAllItemsOfQueryingUser(int, java.lang.String)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<RecommendationItem> getAllItemsOfQueryingUser(final int count, final String username) {
		
		final DBSession mainSession = this.openMainSession();
		try {
			// get bookmarks of user
			final BookmarkParam bookmarkParam = new BookmarkParam();
			bookmarkParam.setRequestedUserName(username);
			bookmarkParam.setGroupId(GroupID.PUBLIC.getId());
			bookmarkParam.setOffset(0);
			bookmarkParam.setLimit(count);
			
			List<Post<Bookmark>> bookmarkResults = (List<Post<Bookmark>>) this.queryForList("getReducedUserBookmark", bookmarkParam, mainSession);
			List<RecommendationItem> items = new ArrayList<RecommendationItem>();
			for(Post<Bookmark> bookmark : bookmarkResults) {
				RecommendationItem item =  new RecommendationPost(bookmark);
				items.add(item);
			}
			
			//get publications of user
			final BibTexParam bibtexParam = new BibTexParam();
			bibtexParam.setRequestedUserName(username);
			bibtexParam.setGroupId(GroupID.PUBLIC.getId());
			bibtexParam.setOffset(0);
			bibtexParam.setLimit(count);
			
			List<Post<BibTex>> bibtexResults = (List<Post<BibTex>>) this.queryForList("getReducedUserBibTex", bibtexParam, mainSession);
			for(Post<BibTex> bibtex : bibtexResults) {
				RecommendationItem item =  new RecommendationPost(bibtex);
				items.add(item);
			}
			
			return items;
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
	
	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.database.RecommenderMainItemAccess#getItemsForContentBasedFiltering(int, recommender.core.interfaces.model.ItemRecommendationEntity)
	 */
	@Override
	public abstract Collection<RecommendationItem> getItemsForContentBasedFiltering(final int maxItemsToEvaluate, final ItemRecommendationEntity entity);
	
	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.database.RecommenderMainItemAccess#getTaggedItems(int, java.util.Set)
	 */
	@Override
	public abstract List<RecommendationItem> getTaggedItems(final int maxItemsToEvaluate, final Set<String> tags);
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.recommender.connector.database.ExtendedMainAccess#getItemByTitle(java.lang.String)
	 */
	@Override
	public abstract RecommendationItem getItemByTitle(final String title);
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.recommender.connector.database.ExtendedMainAccess#getItemByUserWithHash(java.lang.String, java.lang.String)
	 */
	@Override
	public abstract RecommendationItem getItemByUserIdWithHash(final String hash, final String username);
}
