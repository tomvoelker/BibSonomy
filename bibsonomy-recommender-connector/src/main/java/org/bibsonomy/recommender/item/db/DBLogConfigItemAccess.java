package org.bibsonomy.recommender.item.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import recommender.core.database.params.RecQuerySettingParam;
import recommender.core.database.params.ResultParam;
import recommender.core.interfaces.factories.RecommenderItemFactory;
import recommender.core.interfaces.model.ItemRecommendationEntity;
import recommender.core.interfaces.model.RecommendationItem;
import recommender.impl.database.DBLogConfigAccess;
import recommender.impl.model.RecommendedItem;

/**
 * 
 * TODO: add documentation to this class
 *
 * @author lha
 */
public class DBLogConfigItemAccess extends DBLogConfigAccess<ItemRecommendationEntity, RecommendedItem>{
	
	private RecommenderItemFactory itemFactory;
	
	public RecommenderItemFactory getItemFactory() {
		return itemFactory;
	}
	
	public void setItemFactory(RecommenderItemFactory itemFactory) {
		this.itemFactory = itemFactory;
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.impl.database.DBLogConfigAccess#getRecommendations(java.lang.Long, java.lang.Long, java.util.Collection)
	 */
	@Override
	public void getRecommendations(Long qid, Long sid, Collection<RecommendedItem> recommendedItems) {
		// print out newly added recommendations
		final RecQuerySettingParam queryMap = new RecQuerySettingParam();
		queryMap.setQid(qid);
		queryMap.setSid(sid);
		final List<ResultParam> queryResult = this.manager.processQueryForList(ResultParam.class, "getRecommendationsByQidSid", queryMap);
		for (final ResultParam result : queryResult) {
			final RecommendationItem temp = itemFactory.getInstanceOfRecommendationItem();
			temp.setId(String.valueOf(result.getResultId()));
			temp.setTitle(result.getResultTitle());
			final RecommendedItem recItem = new RecommendedItem(temp);
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
	public void getRecommendations(Long qid, Collection<RecommendedItem> recommendedItems) {
		final List<ResultParam> queryResult = this.manager.processQueryForList(ResultParam.class, "getRecommendationsByQid", qid);	
		for (ResultParam result : queryResult) {
			RecommendationItem temp = itemFactory.getInstanceOfRecommendationItem();
			temp.setId(""+result.getResultId());
			temp.setTitle(result.getResultTitle());
			RecommendedItem recItem = new RecommendedItem(temp);
			recItem.setConfidence(result.getConfidence());
			recItem.setScore(result.getScore());
			recommendedItems.add(recItem);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.impl.database.DBLogConfigAccess#getSelectedResults(java.lang.Long)
	 */
	@Override
	public List<RecommendedItem> getSelectedResults(Long qid) {
		final List<ResultParam> queryResult = this.manager.processQueryForList(ResultParam.class, "getSelectedRecommendationsByQid", qid);	
		List<RecommendedItem> recommendations = new ArrayList<RecommendedItem>();	
		for(ResultParam result : queryResult) {
			RecommendationItem temp = itemFactory.getInstanceOfRecommendationItem();
			temp.setId(""+result.getResultId());
			temp.setTitle(result.getResultTitle());
			RecommendedItem recItem = new RecommendedItem(temp);
			recItem.setConfidence(result.getConfidence());
			recItem.setScore(result.getScore());
			recommendations.add(recItem);
		}	
		return recommendations;
	}
}
