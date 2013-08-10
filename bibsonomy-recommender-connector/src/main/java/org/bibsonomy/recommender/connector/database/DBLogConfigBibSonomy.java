package org.bibsonomy.recommender.connector.database;

import static org.bibsonomy.util.ValidationUtils.present;

import java.sql.Timestamp;
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
import org.bibsonomy.model.Resource;
import org.bibsonomy.recommender.connector.database.params.BibRecQueryParam;
import org.bibsonomy.recommender.connector.database.params.PostRecParam;
import org.bibsonomy.recommender.connector.database.params.RecommendedTagParam;
import org.bibsonomy.recommender.connector.filter.PostPrivacyFilter;
import org.bibsonomy.recommender.connector.model.PostWrapper;
import org.bibsonomy.recommender.connector.model.ResourceWrapper;

import recommender.core.database.RecommenderDBSession;
import recommender.core.database.params.EntityParam;
import recommender.core.database.params.RecQueryParam;
import recommender.core.database.params.RecQuerySettingParam;
import recommender.core.database.params.ResultParam;
import recommender.core.interfaces.model.TagRecommendationEntity;
import recommender.core.model.RecommendedTag;
import recommender.impl.database.DBLogConfigTagAccess;

/**
 * @author Lukas
 * @version $Id$
 */
public class DBLogConfigBibSonomy extends DBLogConfigTagAccess {
	private static final Log log = LogFactory.getLog(DBLogConfigBibSonomy.class);
	
	private static final int CONTENT_TYPE_BOOKMARK = 1;
	private static final int CONTENT_TYPE_BIBTEX = 2;
	
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

		// insert recommender query
		final RecommenderDBSession recommenderSession = this.openRecommenderSession();
		try {
			final Long queryId = (Long) recommenderSession.insert(
					"addRecommenderQuery", recQuery);

			this.storeRecommendationEntity(userName, queryId, entity, true,
					recommenderSession);

			return queryId;
		} finally {

			recommenderSession.close();

		}
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void connectWithRecommendationEntity(TagRecommendationEntity entity,
			String entityID) {
		final RecommenderDBSession recommenderSession = this.openRecommenderSession();
		try {
			
			if(entity instanceof PostWrapper) {
				Post post = ((PostWrapper) entity).getPost();
				final PostRecParam postMap = new PostRecParam();
				postMap.setUserName(entity.getUserName());
				postMap.setDate(new Date());
				postMap.setPostID(post.getContentId());
				postMap.setHash(post.getResource().getIntraHash());
				
				// insert data
				recommenderSession.insert("connectWithEntity", postMap);
			} else {
				log.error("TagRecommendationentity was not a PostWrapper, this should not happen!");
			}

		} finally {
			recommenderSession.close();
		}
	}
	
	@Override
	public RecQueryParam getQuery(Long qid) {
		final RecommenderDBSession recommenderSession = this.openRecommenderSession();
		try {
			
			BibRecQueryParam param = this.queryForObject("getQueryByID", qid,
					BibRecQueryParam.class, recommenderSession);
			RecQueryParam result = new RecQueryParam();
			result.setEntity_id(""+param.getPost_id());
			result.setContentType(""+param.getContentType());
			result.setQid(param.getQid());
			result.setQueryTimeout(param.getQueryTimeout());
			result.setTimeStamp(param.getTimeStamp());
			result.setUserName(param.getUserName());
			
			return result;
		} finally {
			recommenderSession.close();
		}
	}
	
	@Override
	protected void storeRecommendationEntity(String userName, Long qid,
			TagRecommendationEntity entity, boolean update,
			RecommenderDBSession session) {
		
		if(PostWrapper.class.isAssignableFrom(entity.getClass())) {
			Post post = ((PostWrapper) entity).getPost();
			if(post != null && Bookmark.class.isAssignableFrom(post.getResource().getClass())) {
				Post<Bookmark> bookmarkPost = (Post<Bookmark>) post;
				storeBookmarkPost(userName, qid, bookmarkPost, "", update, session);
			} else if(post != null && BibTex.class.isAssignableFrom(post.getResource().getClass())) {
				Post<BibTex> bibtexPost = (Post<BibTex>) post;
				storeBibTexPost(userName, qid, bibtexPost, "", update, session);
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
	private void storeBibTexPost(final String userName, final Long qid, final Post<BibTex> post, final String oldHash, final boolean update, final RecommenderDBSession session) {
		log.warn("storeBibTexPost not tested.");
		final BibTexParam param = new BibTexParam();
		param.setResource(post.getResource());
		param.setRequestedContentId(qid.intValue());
		param.setDescription(post.getDescription());
		param.setDate(post.getDate());
		param.setUserName(((post.getUser() != null) ? post.getUser().getName() : ""));
		
		try {
			session.beginTransaction();
			session.insert("insertBibTex", param);
			session.commitTransaction();
		} finally {
			// all done -> close session
			session.endTransaction();
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
	private void storeBookmarkPost(final String userName, final Long qid, final Post<Bookmark> post, final String oldHash, final boolean update, final RecommenderDBSession session) {
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
		
		try {
			session.beginTransaction();
			session.insert("insertBookmark", param);
			session.commitTransaction();
		} finally {
			// all done -> close session
			session.endTransaction();
		}
	}
	
	@Override
	public boolean logRecommendation(Long qid, Long sid, long latency,
			SortedSet<RecommendedTag> results, SortedSet<RecommendedTag> preset) {
		final RecommenderDBSession recommenderSession = this.openRecommenderSession();
		try {
			recommenderSession.beginTransaction();

			// log each recommended tag
			final RecommendedTagParam response = new RecommendedTagParam();
			response.setQid(qid);
			response.setSid(sid);
			response.setLatency(latency);
			for (final RecommendedTag result : results) {
				response.setTagName(result.getName());
				response.setConfidence(result.getConfidence());
				response.setScore(result.getScore());
				this.insert("addRecommenderResponse", response,
						recommenderSession);
			}

			recommenderSession.commitTransaction();
			return false;
		} finally {
			recommenderSession.endTransaction();
			recommenderSession.close();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see recommender.core.database.DBLogic#getRecommendations(java.lang.Long, java.lang.Long, java.util.Collection)
	 */
	@Override
	public void getRecommendations(Long qid, Long sid,
			Collection<RecommendedTag> recommendedTags) {
		final RecommenderDBSession recommenderSession = this.openRecommenderSession();
		try {
			// TODO ugly inefficient implementation
			log.warn("Inefficient implementation");

			// print out newly added recommendations
			final RecQuerySettingParam queryMap = new RecQuerySettingParam();
			queryMap.setQid(qid);
			queryMap.setSid(sid);
			final List<RecommendedTag> queryResult = this.queryForList(
					"getRecommendationsByQidSid", queryMap,
					RecommendedTag.class, recommenderSession);
			recommendedTags.addAll(queryResult);
		} finally {
			recommenderSession.close();
		}
	}

	@Override
	public void getRecommendations(Long qid,
			Collection<RecommendedTag> recommendedTags) {
		final RecommenderDBSession recommenderSession = this.openRecommenderSession();
		try {
			// TODO ugly inefficient implementation
			log.warn("Inefficient implementation");
			final List<RecommendedTag> queryResult = this.queryForList(
					"getRecommendationsByQid", qid, RecommendedTag.class,
					recommenderSession);
			recommendedTags.addAll(queryResult);
		} finally {
			recommenderSession.close();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.database.DBLogic#getSelectedTags(java.lang.Long)
	 */
	@Override
	public List<RecommendedTag> getSelectedResults(Long qid) {
		final RecommenderDBSession recommenderSession = this.openRecommenderSession();
		try {
			return this.queryForList("getSelectedRecommendationsByQid", qid,
					RecommendedTag.class, recommenderSession);
		} finally {
			recommenderSession.close();
		}
	}
	
	@Override
	public int storeRecommendation(Long qid, Long rid,
			Collection<RecommendedTag> results) {
		final RecommenderDBSession recommenderSession = this.openRecommenderSession();
		try {
			recommenderSession.beginTransaction();
			recommenderSession.startBatch();

			// set store applied selection strategie's id
			this.setResultSelectorToQuery(qid, rid);

			// insert recommender response
			// #qid#, #score#, #confidence#, #tagName# )
			final RecommendedTagParam response = new RecommendedTagParam();
			response.setQid(qid);
			response.setSid(rid);
			for (final RecommendedTag result : results) {
				response.setScore(result.getScore());
				response.setConfidence(result.getConfidence());
				response.setTagName(result.getName());
				this.insert("addSelectedResult", response, recommenderSession);
			}
			recommenderSession.executeBatch();
			recommenderSession.commitTransaction();

			return results.size();
		} finally {
			recommenderSession.endTransaction();
			recommenderSession.close();
		}
	}
	
	@Override
	public int addRecommendation(Long queryId, Long settingsId,
			SortedSet<RecommendedTag> results, long latency) {
		if (results == null) {
			return 0;
		}

		final RecommenderDBSession recommenderSession = this.openRecommenderSession();
		try {
			recommenderSession.beginTransaction();
			recommenderSession.startBatch();
			
			// log each recommended tag
			final RecommendedTagParam response = new RecommendedTagParam();
			response.setQid(queryId);
			response.setSid(settingsId);
			response.setLatency(latency);
			for (final RecommendedTag result : results) {
				response.setTagName(result.getName());
				response.setConfidence(result.getConfidence());
				response.setScore(result.getScore());
				this.insert("addRecommenderResponse", response,
						recommenderSession);
			}
			
			recommenderSession.executeBatch();
			recommenderSession.commitTransaction();
			return results.size();
		} finally {
			recommenderSession.endTransaction();
			recommenderSession.close();
		}
	}
	
}
