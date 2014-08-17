package org.bibsonomy.recommender.item;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Resource;
import org.bibsonomy.recommender.item.model.RecommendationUser;
import org.bibsonomy.recommender.item.model.RecommendedPost;
import org.bibsonomy.recommender.item.service.RecommenderMainItemAccess;

import recommender.core.Recommender;
import recommender.core.database.DBLogic;
import recommender.core.interfaces.model.ItemRecommendationEntity;
import recommender.core.util.RecommendationResultComparator;

/**
 * Implementation of general routines for every item recommender.
 * 
 * @author lukas
 *
 */
public abstract class AbstractItemRecommender implements Recommender<RecommendationUser, RecommendedPost<? extends Resource>> {
	private static final Log log = LogFactory.getLog(AbstractItemRecommender.class);
	
	/**
	 * The maximal number of items the recommender shall return on a call to
	 * {@link #getRecommendation(ItemRecommendationEntity)}.
	 */
	protected int numberOfItemsToRecommend = Recommender.DEFAULT_NUMBER_OF_RESULTS_TO_RECOMMEND;
	
	protected RecommenderMainItemAccess dbAccess;
	protected DBLogic<RecommendationUser, RecommendedPost<? extends Resource>> dbLogic;
	
	/*
	 * (non-Javadoc)
	 * @see recommender.core.Recommender#getRecommendation(recommender.core.interfaces.model.RecommendationEntity)
	 */
	@Override
	public SortedSet<RecommendedPost<? extends Resource>> getRecommendation(RecommendationUser entity) {
		final SortedSet<RecommendedPost<? extends Resource>> recommendedItems = new TreeSet<RecommendedPost<? extends Resource>>(new RecommendationResultComparator<RecommendedPost<? extends Resource>>());
		this.addRecommendation(recommendedItems, entity);
		
		return recommendedItems;
	}
	
	/*
	 * (non-Javadoc)
	 * @see recommender.core.Recommender#addRecommendation(java.util.Collection, recommender.core.interfaces.model.RecommendationEntity)
	 */
	@Override
	public void addRecommendation(Collection<RecommendedPost<? extends Resource>> recommendations, RecommendationUser entity) {
		log.debug("Getting item recommendations for " + entity);
		this.addRecommendedItemsInternal(recommendations, entity);
		if (log.isDebugEnabled()) log.debug("Recommending items " + recommendations);
	}

	protected abstract void addRecommendedItemsInternal(Collection<RecommendedPost<? extends Resource>> recommendations, RecommendationUser entity);
	
	/*
	 * (non-Javadoc)
	 * @see recommender.core.Recommender#setFeedback(recommender.core.interfaces.model.RecommendationEntity)
	 */
	@Override
	public void setFeedback(final RecommendationUser entity, final RecommendedPost<? extends Resource> item) {
		log.debug("got entity for itemrecommendation with id " + entity + " as feedback.");
		this.setFeedbackInternal(entity, item);
	}

	protected abstract void setFeedbackInternal(RecommendationUser entity, RecommendedPost<? extends Resource> item);
	
	/**
	 * @param dbAccess the main access object to set
	 */
	public void setDbAccess(RecommenderMainItemAccess dbAccess) {
		this.dbAccess = dbAccess;
	}
	
	/**
	 * @param dbLogic the log access object to set
	 */
	public void setDbLogic(DBLogic<RecommendationUser, RecommendedPost<? extends Resource>> dbLogic) {
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
