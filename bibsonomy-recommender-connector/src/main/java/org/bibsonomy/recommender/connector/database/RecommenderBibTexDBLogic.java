package org.bibsonomy.recommender.connector.database;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.recommender.connector.database.params.DummyRecommendationRequestParam;
import org.bibsonomy.recommender.connector.database.params.RecommendationBibTexParam;
import recommender.core.database.RecommenderDBSession;
import recommender.core.interfaces.model.ItemRecommendationEntity;
import recommender.core.interfaces.model.RecommendationItem;

/**
 * 
 * This class implements the database access on the bibsonomy database
 *  for the recommendation library
 * 
 * @author Lukas
 *
 */

public class RecommenderBibTexDBLogic extends RecommenderDBLogic{
	
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
			List<RecommendationBibTexParam> results = this.queryForList("getMostActualBibTex", param, RecommendationBibTexParam.class, mainSession);
			List<RecommendationItem> resources = new ArrayList<RecommendationItem>(results.size());
			for(RecommendationBibTexParam item : results) {
				resources.add(item.getCorrespondingRecommendationItem());
			}
			
			return resources;
		} finally {
			mainSession.close();
		}
	}

}
