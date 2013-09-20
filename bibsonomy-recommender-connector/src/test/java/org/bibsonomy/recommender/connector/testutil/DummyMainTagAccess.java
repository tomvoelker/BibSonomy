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
