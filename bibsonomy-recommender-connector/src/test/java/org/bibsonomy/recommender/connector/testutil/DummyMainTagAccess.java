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
package org.bibsonomy.recommender.connector.testutil;

import java.util.List;

import recommender.core.interfaces.database.RecommenderMainTagAccess;
import recommender.core.interfaces.model.TagRecommendationEntity;
import recommender.core.model.Pair;

public class DummyMainTagAccess implements RecommenderMainTagAccess{

	@Override
	public List<Pair<String, Integer>> getMostPopularTagsForUser(
			String username, int range) {
		// do nothing
		return null;
	}

	@Override
	public List<Pair<String, Integer>> getMostPopularTagsForRecommendationEntity(
			TagRecommendationEntity entity, String entityId, int range) {
		// do nothing
		return null;
	}

	@Override
	public Integer getNumberOfTagsForUser(String username) {
		// do nothing
		return null;
	}

	@Override
	public Integer getNumberOfTaggingsForUser(String username) {
		// do nothing
		return null;
	}

	@Override
	public Integer getNumberOfTagsForRecommendationEntity(
			TagRecommendationEntity entity, String entityId) {
		// do nothing
		return null;
	}

	@Override
	public Integer getNumberOfTagAssignmentsForRecommendationEntity(
			TagRecommendationEntity entity, String entitiyId) {
		// do nothing
		return null;
	}

	@Override
	public Integer getUserIDByName(String userName) {
		// do nothing
		return null;
	}

	@Override
	public String getUserNameByID(int userID) {
		// do nothing
		return null;
	}

	@Override
	public List<String> getTagNamesForRecommendationEntity(Integer entityId) {
		// do nothing
		return null;
	}

	
}
