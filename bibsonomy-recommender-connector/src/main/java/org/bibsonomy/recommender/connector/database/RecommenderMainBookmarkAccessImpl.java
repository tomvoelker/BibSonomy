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
package org.bibsonomy.recommender.connector.database;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.database.common.params.beans.TagIndex;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
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
			bookmarkParam.setUserName(entity.getUserName());
			bookmarkParam.setOffset(0);
			bookmarkParam.setLimit(2*count);
			bookmarkParam.setSimHash(HashID.INTRA_HASH);
			
			List<Post<Bookmark>> results = (List<Post<Bookmark>>) this.queryForList("getBookmarkForHomepage", bookmarkParam, mainSession);
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
	private List<RecommendationItem> getItemsForUsers(final int count, final List<String> usernames) {
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
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.recommender.connector.database.AbstractRecommenderMainItemAccessImpl#getItemsForContentBasedFiltering(int, recommender.core.interfaces.model.ItemRecommendationEntity)
	 */
	@Override
	public Collection<RecommendationItem> getItemsForContentBasedFiltering(final int maxItemsToEvaluate, final ItemRecommendationEntity entity) {
		final List<String> similarUsers = this.getSimilarUsers(USERS_TO_EVALUATE, entity);
		if(present(similarUsers)) {
			return this.getItemsForUsers(maxItemsToEvaluate/similarUsers.size(), similarUsers);
		}
		return new ArrayList<RecommendationItem>();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.recommender.connector.database.AbstractRecommenderMainItemAccessImpl#getTaggedItems(int, java.util.Set)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<RecommendationItem> getTaggedItems(final int maxItemsToEvaluate, final Set<String> tags) {
		final DBSession mainSession = this.openMainSession();
		final List<RecommendationItem> items = new ArrayList<RecommendationItem>();
		try {
			final BookmarkParam param = new BookmarkParam();
			param.setLimit(maxItemsToEvaluate/tags.size());
			param.setOffset(0);
			param.setContentType(ConstantID.BOOKMARK_CONTENT_TYPE);
			param.setGroupId(GroupID.PUBLIC.getId());
			param.setCaseSensitiveTagNames(false);
			final List<TagIndex> tagIndeces = new ArrayList<TagIndex>();
			TagIndex index;
			for(String tag : tags) {
				tagIndeces.clear();
				index = new TagIndex(tag, 1);
				tagIndeces.add(index);
				param.setTagIndex(tagIndeces);
				List<Post> bibtexs = this.queryForList("getBookmarkByTagNames", param, Post.class, mainSession);
				for(Post post : bibtexs) {
					items.add(new RecommendationPost(post));
				}
			}
			return items;
		} finally {
			mainSession.close();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.recommender.connector.database.AbstractRecommenderMainItemAccessImpl#getItemByUserWithHash(java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public RecommendationItem getItemByUserIdWithHash(final String hash, final String userId) {
		final DBSession mainSession = this.openMainSession();
		try {
			final BookmarkParam param = new BookmarkParam();
			param.setHash(hash);
			param.setRequestedUserName(userId);
			param.setSimHash(HashID.INTRA_HASH);
			param.setOffset(0);
			param.setLimit(1);
			
			Post<? extends Resource> post = (Post<? extends Resource>) this.queryForObject("getBookmarkByHashForUserId", param, Post.class, mainSession);
			
			if(post != null) {
				return new RecommendationPost(post);
			}
			return null;
		} finally {
			mainSession.close();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bibsonomy.recommender.connector.database.AbstractRecommenderMainItemAccessImpl#getItemByTitle(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public RecommendationItem getItemByTitle(final String title) {
		final DBSession mainSession = this.openMainSession();
		try {
			final BookmarkParam param = new BookmarkParam();
			param.setTitle(title);
			param.setSimHash(HashID.INTRA_HASH);
			param.setGroupId(GroupID.PUBLIC.getId());
			param.setOffset(0);
			param.setLimit(1);
			
			Post<? extends Resource> post = (Post<? extends Resource>) this.queryForObject("getBookmarkByTitle", param, Post.class, mainSession);
			
			if(post != null) {
				return new RecommendationPost(post);
			}
			return null;
		} finally {
			mainSession.close();
		}
	}
}
