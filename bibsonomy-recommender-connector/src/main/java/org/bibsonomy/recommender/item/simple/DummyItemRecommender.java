package org.bibsonomy.recommender.item.simple;

import java.util.Collection;
import java.util.List;

import org.bibsonomy.recommender.item.AbstractItemRecommender;

import recommender.core.interfaces.model.ItemRecommendationEntity;
import recommender.core.interfaces.model.RecommendationItem;
import recommender.impl.model.RecommendedItem;

/**
 * Dummy recommender implementation which delivers the numberOfItemsToRecommend count of most actual items.
 * Can be used as fallback recommender.
 * 
 * @author lukas
 *
 */
public class DummyItemRecommender extends AbstractItemRecommender {

	private static final String INFO = "This Itemrecommender returns the numberOfResultsToRecommend most actual itemsfrom the database";

	/*
	 * (non-Javadoc)
	 * @see recommender.impl.item.AbstractItemRecommender#addRecommendedItemsInternal(java.util.Collection, recommender.core.interfaces.model.ItemRecommendationEntity)
	 */
	@Override
	protected void addRecommendedItemsInternal(Collection<RecommendedItem> recommendations, ItemRecommendationEntity entity) {
		final List<RecommendationItem> items = this.dbAccess.getMostActualItems(this.numberOfItemsToRecommend, entity);
		int counter = 1;
		for (RecommendationItem item : items) {
			RecommendedItem recommendation = new RecommendedItem(item);
			recommendation.setScore(1.0/(counter + 100));
			recommendations.add(recommendation);
			counter++;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.impl.item.AbstractItemRecommender#setFeedbackInternal(recommender.core.interfaces.model.ItemRecommendationEntity)
	 */
	@Override
	protected void setFeedbackInternal(ItemRecommendationEntity entity, RecommendedItem item) {
		/*
		 * ignore feedback
		 */
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.Recommender#getInfo()
	 */
	@Override
	public String getInfo() {
		return INFO;
	}
}
