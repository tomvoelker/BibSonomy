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
import recommender.core.util.RecommendationResultComparator;

/**
 * Implementation of general routines for every item recommender.
 * 
 * @author lukas
 *
 */
public abstract class AbstractItemRecommender<T extends Resource> implements Recommender<RecommendationUser, RecommendedPost<T>> {
	private static final Log log = LogFactory.getLog(AbstractItemRecommender.class);
	
	/**
	 * The maximal number of items the recommender shall return on a call to
	 * {@link #getRecommendation(ItemRecommendationEntity)}.
	 */
	protected int numberOfItemsToRecommend = Recommender.DEFAULT_NUMBER_OF_RESULTS_TO_RECOMMEND;
	
	protected RecommenderMainItemAccess<T> dbAccess;
	protected DBLogic<RecommendationUser, RecommendedPost<T>> dbLogic;
	
	/*
	 * (non-Javadoc)
	 * @see recommender.core.Recommender#getRecommendation(recommender.core.interfaces.model.RecommendationEntity)
	 */
	@Override
	public SortedSet<RecommendedPost<T>> getRecommendation(RecommendationUser entity) {
		final SortedSet<RecommendedPost<T>> recommendedItems = new TreeSet<RecommendedPost<T>>(new RecommendationResultComparator<RecommendedPost<T>>());
		this.addRecommendation(recommendedItems, entity);
		
		return recommendedItems;
	}
	
	/*
	 * (non-Javadoc)
	 * @see recommender.core.Recommender#addRecommendation(java.util.Collection, recommender.core.interfaces.model.RecommendationEntity)
	 */
	@Override
	public void addRecommendation(Collection<RecommendedPost<T>> recommendations, RecommendationUser entity) {
		log.debug("Getting item recommendations for " + entity);
		this.addRecommendedItemsInternal(recommendations, entity);
		if (log.isDebugEnabled()) log.debug("Recommending items " + recommendations);
	}

	protected abstract void addRecommendedItemsInternal(Collection<RecommendedPost<T>> recommendations, RecommendationUser entity);
	
	/*
	 * (non-Javadoc)
	 * @see recommender.core.Recommender#setFeedback(recommender.core.interfaces.model.RecommendationEntity)
	 */
	@Override
	public void setFeedback(final RecommendationUser entity, final RecommendedPost<T> item) {
		log.debug("got entity for itemrecommendation with id " + entity + " as feedback.");
		this.setFeedbackInternal(entity, item);
	}

	protected abstract void setFeedbackInternal(RecommendationUser entity, RecommendedPost<T> item);
	
	/**
	 * @param dbAccess the main access object to set
	 */
	public void setDbAccess(RecommenderMainItemAccess<T> dbAccess) {
		this.dbAccess = dbAccess;
	}
	
	/**
	 * @param dbLogic the log access object to set
	 */
	public void setDbLogic(DBLogic<RecommendationUser, RecommendedPost<T>> dbLogic) {
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
