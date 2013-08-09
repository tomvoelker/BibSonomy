package org.bibsonomy.recommender.connector.filter;

import recommender.core.interfaces.filter.PrivacyFilter;
import recommender.core.interfaces.model.ItemRecommendationEntity;

public class UserPrivacyFilter implements PrivacyFilter<ItemRecommendationEntity>{

	@Override
	public ItemRecommendationEntity filterEntity(ItemRecommendationEntity entity) {
		return entity;
	}

}
