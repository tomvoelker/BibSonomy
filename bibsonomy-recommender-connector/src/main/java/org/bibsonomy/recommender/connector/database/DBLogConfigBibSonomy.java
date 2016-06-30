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

import static org.bibsonomy.util.ValidationUtils.present;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.recommender.connector.database.params.BibRecQueryParam;
import org.bibsonomy.recommender.connector.database.params.PostRecParam;
import org.bibsonomy.recommender.connector.database.params.RecommendedTagParam;
import org.bibsonomy.recommender.connector.model.PostWrapper;

import recommender.core.database.params.RecQueryParam;
import recommender.core.database.params.RecQuerySettingParam;
import recommender.core.interfaces.model.TagRecommendationEntity;
import recommender.core.model.Pair;
import recommender.impl.database.DBLogConfigTagAccess;
import recommender.impl.model.RecommendedTag;

/**
 * This implements the old logging and configuration scheme for tag recommendations,
 * known from bibsonomy-recommender.
 * As a result the old tables can stay in use.
 * 
 */
public class DBLogConfigBibSonomy extends DBLogConfigTagAccess {
	private static final Log log = LogFactory.getLog(DBLogConfigBibSonomy.class);
	
	private static final int CONTENT_TYPE_BOOKMARK = 1;
	private static final int CONTENT_TYPE_BIBTEX = 2;
	
	/*
	 * (non-Javadoc)
	 * @see recommender.impl.database.DBLogConfigAccess#addQuery(java.lang.String, java.util.Date, recommender.core.interfaces.model.RecommendationEntity, java.lang.String, int)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Long addQuery(String userName, Date date,
			TagRecommendationEntity entity, String entityID, int timeout) {
		// construct parameter
		final BibRecQueryParam recQuery = new BibRecQueryParam();
		recQuery.setTimeStamp(new Timestamp(date.getTime()));
		recQuery.setUserName(userName);
		recQuery.setPost_id(Integer.parseInt(entityID));
		
		if(entity instanceof PostWrapper) {
			if(((PostWrapper) entity).getPost() instanceof Post) {
				if(((PostWrapper) entity).getPost().getResource() instanceof BibTex) {
					recQuery.setContentType(CONTENT_TYPE_BIBTEX);
				} else if(((PostWrapper) entity).getPost().getResource() instanceof Bookmark) {
					recQuery.setContentType(CONTENT_TYPE_BOOKMARK);
				}
			}
		}
		
		recQuery.setQueryTimeout(timeout);
		final Long queryId = (Long) this.manager.processInsertQuery("addRecommenderQuery", recQuery);

		this.storeRecommendationEntity(userName, queryId, entity, true);

		return queryId;
	}
	
	/*
	 * (non-Javadoc)
	 * @see recommender.impl.database.DBLogConfigTagAccess#addFeedback(recommender.core.interfaces.model.TagRecommendationEntity, recommender.core.model.RecommendedTag)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void addFeedback(TagRecommendationEntity entity, RecommendedTag result) {
		if (entity instanceof PostWrapper) {
			Post post = ((PostWrapper) entity).getPost();
			final PostRecParam postMap = new PostRecParam();
			postMap.setUserName(entity.getUserName());
			postMap.setDate(new Date());
			postMap.setPostID(post.getContentId());
			postMap.setHash(post.getResource().getIntraHash());
		
			// insert data
			this.manager.processInsertQuery("connectWithEntity", postMap);
		} else {
			log.error("TagRecommendationentity was not a PostWrapper, this should not happen!");
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see recommender.impl.database.DBLogConfigAccess#getQuery(java.lang.Long)
	 */
	@Override
	public RecQueryParam getQuery(Long qid) {
		BibRecQueryParam param = this.manager.processQueryForObject(BibRecQueryParam.class, "getQueryByID", qid);
		RecQueryParam result = new RecQueryParam();
		result.setEntity_id(""+param.getPost_id());
		result.setContentType(""+param.getContentType());
		result.setQid(param.getQid());
		result.setQueryTimeout(param.getQueryTimeout());
		result.setTimeStamp(param.getTimeStamp());
		result.setUserName(param.getUserName());
		
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see recommender.impl.database.DBLogConfigAccess#storeRecommendationEntity(java.lang.String, java.lang.Long, recommender.core.interfaces.model.RecommendationEntity, boolean, recommender.core.database.RecommenderDBSession)
	 */
	@Override
	protected void storeRecommendationEntity(String userName, Long qid, TagRecommendationEntity entity, boolean update) {
		if (PostWrapper.class.isAssignableFrom(entity.getClass())) {
			final Post<?> post = ((PostWrapper<?>) entity).getPost();
			if (post != null && Bookmark.class.isAssignableFrom(post.getResource().getClass())) {
				@SuppressWarnings("unchecked")
				final Post<Bookmark> bookmarkPost = (Post<Bookmark>) post;
				storeBookmarkPost(userName, qid, bookmarkPost, "", update);
			} else if (post != null && BibTex.class.isAssignableFrom(post.getResource().getClass())) {
				@SuppressWarnings("unchecked")
				final Post<BibTex> bibtexPost = (Post<BibTex>) post;
				storeBibTexPost(userName, qid, bibtexPost, "", update);
			}
		}
	}
	
	/**
	 * Store post for current recommendation.
	 * @param userName
	 * @param post
	 * @param oldHash
	 * @param update
	 * @param session
	 */
	private void storeBibTexPost(final String userName, final Long qid, final Post<BibTex> post, final String oldHash, final boolean update) {
		log.warn("storeBibTexPost not tested.");
		final BibTexParam param = new BibTexParam();
		param.setResource(post.getResource());
		param.setRequestedContentId(qid.intValue());
		param.setDescription(post.getDescription());
		param.setDate(post.getDate());
		param.setUserName(((post.getUser() != null) ? post.getUser().getName() : ""));
		this.manager.processInsertQuery("insertBibTex", param);
	}

	/**
	 * Store post for current recommendation.
	 * @param userName
	 * @param post
	 * @param oldHash
	 * @param update
	 * @param session
	 */
	private void storeBookmarkPost(final String userName, final Long qid, final Post<Bookmark> post, final String oldHash, final boolean update) {
		final BookmarkParam param = new BookmarkParam();
		param.setResource(post.getResource());
		param.setDate(post.getDate());
		param.setRequestedContentId(qid.intValue());
		param.setHash(post.getResource().getIntraHash());
		param.setDescription(post.getDescription());
		param.setUserName(post.getUser().getName());
		param.setUrl(post.getResource().getUrl());
		// TODO: adapt to multiple groups?
		// in field group in table bookmark, insert the id for PUBLIC, PRIVATE or the id of the FIRST group in list
		final Set<Group> groups = post.getGroups();
		if (present(groups)) {
			final int groupId =  groups.iterator().next().getGroupId();
			param.setGroupId(groupId);
		}
		this.manager.processInsertQuery("insertBookmark", param);
	}
	
	/*
	 * (non-Javadoc)
	 * @see recommender.impl.database.DBLogConfigAccess#logRecommendation(java.lang.Long, java.lang.Long, long, java.util.SortedSet, java.util.SortedSet)
	 */
	@Override
	public boolean logRecommendation(Long qid, Long sid, long latency, SortedSet<RecommendedTag> results, SortedSet<RecommendedTag> preset) {
		// log each recommended tag
		final RecommendedTagParam response = new RecommendedTagParam();
		response.setQid(qid);
		response.setSid(sid);
		response.setLatency(latency);
		for (final RecommendedTag result : results) {
			response.setTagName(result.getName());
			response.setConfidence(result.getConfidence());
			response.setScore(result.getScore());
			this.manager.processInsertQuery("addRecommenderResponse", response);
		}
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see recommender.core.database.DBLogic#getRecommendations(java.lang.Long, java.lang.Long, java.util.Collection)
	 */
	@Override
	public void getRecommendations(Long qid, Long sid, Collection<RecommendedTag> recommendedTags) {
		// print out newly added recommendations
		final RecQuerySettingParam queryMap = new RecQuerySettingParam();
		queryMap.setQid(qid);
		queryMap.setSid(sid);
		final List<RecommendedTag> queryResult = this.manager.processQueryForList(RecommendedTag.class,
				"getRecommendationsByQidSid", queryMap);
		recommendedTags.addAll(queryResult);
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.impl.database.DBLogConfigTagAccess#getRecommendations(java.lang.Long, java.util.Collection)
	 */
	@Override
	public void getRecommendations(Long qid,
			Collection<RecommendedTag> recommendedTags) {
			final List<RecommendedTag> queryResult = this.manager.processQueryForList(RecommendedTag.class,
					"getRecommendationsByQid", qid);
			recommendedTags.addAll(queryResult);
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.database.DBLogic#getSelectedTags(java.lang.Long)
	 */
	@Override
	public List<RecommendedTag> getSelectedResults(Long qid) {
			return this.manager.processQueryForList(RecommendedTag.class, "getSelectedRecommendationsByQid", qid);
	}
	
	/*
	 * (non-Javadoc)
	 * @see recommender.impl.database.DBLogConfigAccess#storeRecommendation(java.lang.Long, java.lang.Long, java.util.Collection)
	 */
	@Override
	public int storeRecommendation(Long qid, Long rid, Collection<RecommendedTag> results) {
		// set store applied selection strategie's id
		this.setResultSelectorToQuery(qid, rid);

		final List<Pair<String, Object>> executionList = new ArrayList<Pair<String, Object>>();
		// insert recommender response
		for (final RecommendedTag result : results) {
			final RecommendedTagParam response = new RecommendedTagParam();
			response.setQid(qid);
			response.setSid(rid);
			response.setScore(result.getScore());
			response.setConfidence(result.getConfidence());
			response.setTagName(result.getName());
			executionList.add(new Pair<String, Object>("addSelectedResult", response));
		}
		return this.manager.processBatchOfInsertQueries(executionList);
	}
	
	/*
	 * (non-Javadoc)
	 * @see recommender.impl.database.DBLogConfigAccess#addRecommendation(java.lang.Long, java.lang.Long, java.util.SortedSet, long)
	 */
	@Override
	public int addRecommendation(Long queryId, Long settingsId,
			SortedSet<RecommendedTag> results, long latency) {
		if (results == null) {
			return 0;
		}
		final List<Pair<String, Object>> executionList = new ArrayList<Pair<String,Object>>();
		// log each recommended tag
		for (final RecommendedTag result : results) {
			final RecommendedTagParam response = new RecommendedTagParam();
			response.setQid(queryId);
			response.setSid(settingsId);
			response.setLatency(latency);
			response.setTagName(result.getName());
			response.setConfidence(result.getConfidence());
			response.setScore(result.getScore());
			executionList.add(new Pair<String, Object>("addRecommenderResponse", response));
		}
		return this.manager.processBatchOfInsertQueries(executionList);
	}
	
}
