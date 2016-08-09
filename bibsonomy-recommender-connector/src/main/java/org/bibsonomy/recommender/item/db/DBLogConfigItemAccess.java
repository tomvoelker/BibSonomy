package org.bibsonomy.recommender.item.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.recommender.item.model.RecommendationUser;
import org.bibsonomy.recommender.item.model.RecommendedPost;

import recommender.core.database.params.RecQuerySettingParam;
import recommender.core.database.params.ResultParam;
import recommender.impl.database.DBLogConfigAccess;

/**
 * 
 * TODO: add documentation to this class
 *
 * @author lha
 * @param <R> 
 */
public class DBLogConfigItemAccess<R extends Resource> extends DBLogConfigAccess<RecommendationUser, RecommendedPost<R>>{

	/*
	 * (non-Javadoc)
	 * @see recommender.impl.database.DBLogConfigAccess#getRecommendations(java.lang.Long, java.lang.Long, java.util.Collection)
	 */
	@Override
	public void getRecommendations(Long qid, Long sid, Collection<RecommendedPost<R>> recommendedItems) {
		// print out newly added recommendations
		final RecQuerySettingParam queryMap = new RecQuerySettingParam();
		queryMap.setQid(qid);
		queryMap.setSid(sid);
		final List<ResultParam> queryResult = this.manager.processQueryForList(ResultParam.class, "getRecommendationsByQidSid", queryMap);
		convertToRecommendedPosts(recommendedItems, queryResult);
	}

	private void convertToRecommendedPosts(Collection<RecommendedPost<R>> recommendedItems, final List<ResultParam> queryResult) {
		for (final ResultParam result : queryResult) {
			final Post<R> post = new Post<R>();
			// FIXME: refactor init this post correctly
			final RecommendedPost<R> recItem = new RecommendedPost<R>();
			recItem.setPost(post);
			recItem.setConfidence(result.getConfidence());
			recItem.setScore(result.getScore());
			recommendedItems.add(recItem);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.impl.database.DBLogConfigAccess#getRecommendations(java.lang.Long, java.util.Collection)
	 */
	@Override
	public void getRecommendations(Long qid, Collection<RecommendedPost<R>> recommendedItems) {
		final List<ResultParam> queryResult = this.manager.processQueryForList(ResultParam.class, "getRecommendationsByQid", qid);	
		convertToRecommendedPosts(recommendedItems, queryResult);
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.impl.database.DBLogConfigAccess#getSelectedResults(java.lang.Long)
	 */
	@Override
	public List<RecommendedPost<R>> getSelectedResults(Long qid) {
		final List<ResultParam> queryResult = this.manager.processQueryForList(ResultParam.class, "getSelectedRecommendationsByQid", qid);	
		final List<RecommendedPost<R>> recommendations = new ArrayList<RecommendedPost<R>>();
		convertToRecommendedPosts(recommendations, queryResult);
		return recommendations;
	}
}
