package org.bibsonomy.recommender.tag.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.recommender.tag.model.RecommendedTag;

import recommender.core.database.params.ResultParam;
import recommender.impl.database.DBLogConfigAccess;

/**
 * 
 * @author lha
 */
public class DBLogConfigTagAccess extends DBLogConfigAccess<Post<? extends Resource>, RecommendedTag> {

	/* 
	 * TODO: abstract this method
	 * (non-Javadoc)
	 * @see recommender.core.database.DBLogic#getRecommendations(java.lang.Long, java.lang.Long, java.util.Collection)
	 */
	@Override
	public void getRecommendations(Long qid, Long sid, Collection<RecommendedTag> recommendedTags) {
		final List<ResultParam> queryResult = this.manager.processQueryForList(ResultParam.class, "getRecommendationsByQidSid", qid);
		for (ResultParam result : queryResult) {
			RecommendedTag tag = new RecommendedTag();
			tag.setName(result.getResultId());
			tag.setConfidence(result.getConfidence());
			tag.setScore(result.getScore());
			recommendedTags.add(tag);
		}
	}

	/* 
	 * TODO: abstract this method
	 * (non-Javadoc)
	 * @see recommender.impl.database.DBLogConfigAccess#getRecommendations(java.lang.Long, java.util.Collection)
	 */
	@Override
	public void getRecommendations(Long qid, Collection<RecommendedTag> recommendedTags) {
		final List<ResultParam> queryResult = this.manager.processQueryForList(ResultParam.class, "getRecommendationsByQid", qid);
		for(ResultParam result : queryResult) {
			RecommendedTag tag = new RecommendedTag();
			tag.setName(""+result.getResultId());
			tag.setConfidence(result.getConfidence());
			tag.setScore(result.getScore());
			recommendedTags.add(tag);
		}
	}

	/* 
	 * TODO: abstract this method
	 * (non-Javadoc)
	 * @see recommender.core.database.DBLogic#getSelectedTags(java.lang.Long)
	 */
	@Override
	public List<RecommendedTag> getSelectedResults(Long qid) {
		final List<ResultParam> queryResult = this.manager.processQueryForList(ResultParam.class, "getSelectedRecommendationsByQid", qid);
		List<RecommendedTag> recommendations = new ArrayList<RecommendedTag>();
		for(ResultParam result : queryResult) {
			RecommendedTag tag = new RecommendedTag();
			tag.setName(""+result.getResultId());
			tag.setConfidence(result.getConfidence());
			tag.setScore(result.getScore());
			recommendations.add(tag);
		}
		return recommendations;
	}

}
