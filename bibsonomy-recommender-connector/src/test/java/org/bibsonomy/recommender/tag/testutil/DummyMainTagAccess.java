package org.bibsonomy.recommender.tag.testutil;

import java.util.List;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.recommender.tag.service.RecommenderMainTagAccess;

import recommender.core.model.Pair;

public class DummyMainTagAccess implements RecommenderMainTagAccess {

	@Override
	public List<Pair<String, Integer>> getMostPopularTagsForUser(
			String username, int range) {
		// do nothing
		return null;
	}

	@Override
	public List<Pair<String, Integer>> getMostPopularTagsForRecommendationEntity(Post<? extends Resource> entity, String entityId, int range) {
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
	public Integer getNumberOfTagsForRecommendationEntity(Post<? extends Resource> entity, String entityId) {
		// do nothing
		return null;
	}

	@Override
	public Integer getNumberOfTagAssignmentsForRecommendationEntity(Post<? extends Resource> entity, String entitiyId) {
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
