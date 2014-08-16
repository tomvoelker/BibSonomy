package org.bibsonomy.recommender.item;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import recommender.core.Recommender;
import recommender.core.database.DBLogic;
import recommender.core.interfaces.database.RecommenderMainItemAccess;
import recommender.core.interfaces.model.ItemRecommendationEntity;
import recommender.core.util.RecommendationResultComparator;
import recommender.impl.model.RecommendedItem;

/**
 * Implementation of general routines for every item recommender.
 * 
 * @author lukas
 *
 */
public abstract class AbstractItemRecommender implements Recommender<ItemRecommendationEntity, RecommendedItem>{
	private static final Log log = LogFactory.getLog(AbstractItemRecommender.class);
	
	/**
	 * The maximal number of items the recommender shall return on a call to
	 * {@link #getRecommendation(ItemRecommendationEntity)}.
	 */
	protected int numberOfItemsToRecommend = Recommender.DEFAULT_NUMBER_OF_RESULTS_TO_RECOMMEND;
	
	protected RecommenderMainItemAccess dbAccess;
	protected DBLogic<ItemRecommendationEntity, RecommendedItem> dbLogic;
	
	/*
	 * (non-Javadoc)
	 * @see recommender.core.Recommender#getRecommendation(recommender.core.interfaces.model.RecommendationEntity)
	 */
	@Override
	public SortedSet<RecommendedItem> getRecommendation(
			ItemRecommendationEntity entity) {
		final SortedSet<RecommendedItem> recommendedItems = new TreeSet<RecommendedItem>(new RecommendationResultComparator<RecommendedItem>());
		this.addRecommendation(recommendedItems, entity);
		
		return recommendedItems;
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.core.Recommender#addRecommendation(java.util.Collection, recommender.core.interfaces.model.RecommendationEntity)
	 */
	@Override
	public void addRecommendation(Collection<RecommendedItem> recommendations,
			ItemRecommendationEntity entity) {
		log.debug("Getting item recommendations for " + entity);
		this.addRecommendedItemsInternal(recommendations, entity);
		if (log.isDebugEnabled()) log.debug("Recommending items " + recommendations);
	}

	protected abstract void addRecommendedItemsInternal(Collection<RecommendedItem> recommendations,
			ItemRecommendationEntity entity);
	
	/*
	 * (non-Javadoc)
	 * @see recommender.core.Recommender#setFeedback(recommender.core.interfaces.model.RecommendationEntity)
	 */
	@Override
	public void setFeedback(final ItemRecommendationEntity entity, final RecommendedItem item) {
		log.debug("got entity for itemrecommendation with id " + entity + " as feedback.");
		this.setFeedbackInternal(entity, item);
	}

	protected abstract void setFeedbackInternal(ItemRecommendationEntity entity, RecommendedItem item);
	
	/**
	 * @param dbAccess the main access object to set
	 */
	public void setDbAccess(RecommenderMainItemAccess dbAccess) {
		this.dbAccess = dbAccess;
	}
	
	/**
	 * @param dbLogic the log access object to set
	 */
	public void setDbLogic(DBLogic<ItemRecommendationEntity, RecommendedItem> dbLogic) {
		this.dbLogic = dbLogic;
	}
	
	/**
	 * @param numberOfItemsToRecommend the number of items to be recommended
	 */
	public void setNumberOfItemsToRecommend(int numberOfItemsToRecommend) {
		this.numberOfItemsToRecommend = numberOfItemsToRecommend;
	}
	
	/**
	 * @return the number of items to recommend
	 */
	public int getNumberOfItemsToRecommend() {
		return numberOfItemsToRecommend;
	}
	
	/*
	 * (non-Javadoc)
	 * @see recommender.core.Recommender#setNumberOfResultsToRecommend(int)
	 */
	@Override
	public void setNumberOfResultsToRecommend(int numberOfResultsToRecommend) {
		this.numberOfItemsToRecommend = numberOfResultsToRecommend;
	}
}
