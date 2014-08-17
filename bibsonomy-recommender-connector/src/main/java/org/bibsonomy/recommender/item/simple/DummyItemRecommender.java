package org.bibsonomy.recommender.item.simple;

import java.util.Collection;
import java.util.List;

import org.bibsonomy.model.Resource;
import org.bibsonomy.recommender.item.AbstractItemRecommender;
import org.bibsonomy.recommender.item.model.RecommendationUser;
import org.bibsonomy.recommender.item.model.RecommendedPost;

/**
 * Dummy recommender implementation which delivers the numberOfItemsToRecommend count of most actual items.
 * Can be used as fallback recommender.
 * 
 * @author lukas
 */
public class DummyItemRecommender extends AbstractItemRecommender {

	private static final String INFO = "This Itemrecommender returns the numberOfResultsToRecommend most actual itemsfrom the database";

	/*
	 * (non-Javadoc)
	 * @see recommender.impl.item.AbstractItemRecommender#addRecommendedItemsInternal(java.util.Collection, recommender.core.interfaces.model.ItemRecommendationEntity)
	 */
	@Override
	protected void addRecommendedItemsInternal(Collection<RecommendedPost<? extends Resource>> recommendations, RecommendationUser entity) {
		final List<RecommendedPost<? extends Resource>> mostActualItems = this.dbAccess.getMostActualItems(this.numberOfItemsToRecommend, entity);
		int counter = 1;
		for (final RecommendedPost<? extends Resource> item : mostActualItems) {
			item.setScore(1.0 / (counter + 100));
			recommendations.add(item);
			counter++;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.impl.item.AbstractItemRecommender#setFeedbackInternal(recommender.core.interfaces.model.ItemRecommendationEntity)
	 */
	@Override
	protected void setFeedbackInternal(RecommendationUser entity, RecommendedPost<? extends Resource> item) {
		// ignore feedback
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
