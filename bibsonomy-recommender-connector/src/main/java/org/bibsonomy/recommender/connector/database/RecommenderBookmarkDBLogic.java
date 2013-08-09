package org.bibsonomy.recommender.connector.database;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.recommender.connector.database.params.DummyRecommendationRequestParam;
import org.bibsonomy.recommender.connector.database.params.RecommendationBookmarkParam;

import recommender.core.database.RecommenderDBSession;
import recommender.core.interfaces.model.ItemRecommendationEntity;
import recommender.core.interfaces.model.RecommendationItem;

public class RecommenderBookmarkDBLogic extends RecommenderDBLogic {

	/*
	 * (non-Javadoc)
	 * @see recommender.core.interfaces.database.RecommenderDBAccess#getMostActualItems(int)
	 */
	@Override
	public List<RecommendationItem> getMostActualItems(int count, final ItemRecommendationEntity entity) {
		final RecommenderDBSession mainSession = this.openMainSession();
		try {
			final DummyRecommendationRequestParam param = new DummyRecommendationRequestParam();
			param.setCount(count);
			param.setRequestingUserName(entity.getUserName());
			List<RecommendationBookmarkParam> results = this.queryForList("getMostActualBookmark", param, RecommendationBookmarkParam.class, mainSession);
			List<RecommendationItem> resources = new ArrayList<RecommendationItem>(results.size());
			for(RecommendationBookmarkParam item : results) {
				resources.add(item.getCorrespondingRecommendationItem());
			}
			
			return resources;
		} finally {
			mainSession.close();
		}
	}
	
}
