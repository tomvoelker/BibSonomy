package org.bibsonomy.recommender.connector.testutil;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.recommender.connector.database.params.RecommendationBibTexParam;

import recommender.core.interfaces.database.RecommenderDBAccess;
import recommender.core.interfaces.model.ItemRecommendationEntity;
import recommender.core.interfaces.model.RecommendationItem;
import recommender.core.interfaces.model.TagRecommendationEntity;
import recommender.core.temp.copy.common.Pair;

public class DummyDBAccess implements RecommenderDBAccess{

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
	public Integer getNumberOfTasForRecommendationEntity(
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

	@Override
	public List<RecommendationItem> getMostActualItems(int count,
			ItemRecommendationEntity entity) {
		
		ArrayList<RecommendationItem> items = new ArrayList<RecommendationItem>();
		
		for(int i = 0; i < count; i++) {
			RecommendationBibTexParam param = new RecommendationBibTexParam();
			param.setId("testitem");
			param.setTitle("testitem");
			param.setOwnerName("foo.bar");
			items.add(param.getCorrespondingRecommendationItem());
		}
		
		return items;
	}

}
