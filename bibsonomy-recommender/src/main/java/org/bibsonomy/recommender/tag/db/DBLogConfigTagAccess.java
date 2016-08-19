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
