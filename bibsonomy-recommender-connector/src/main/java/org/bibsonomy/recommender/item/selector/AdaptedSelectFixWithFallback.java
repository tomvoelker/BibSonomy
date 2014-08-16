package org.bibsonomy.recommender.item.selector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bibsonomy.common.Pair;
import org.bibsonomy.recommender.connector.model.RecommendationPost;
import org.bibsonomy.recommender.item.service.ExtendedMainAccess;

import recommender.core.interfaces.model.RecommendationItem;
import recommender.core.interfaces.model.RecommendationResult;
import recommender.impl.model.RecommendedItem;
import recommender.impl.multiplexer.RecommendationResultManager;
import recommender.impl.multiplexer.strategy.SelectFixRecommenderWithFallback;

/**
 * This class adapts the SelectFixWithFallback algorithm by appending it with greedy loading in case of the
 * resources were not cached.
 * 
 * @author lukas
 *
 * @param <E>
 * @param <R>
 */
public class AdaptedSelectFixWithFallback<E, R extends RecommendationResult> extends SelectFixRecommenderWithFallback<E, R> {
	
	private ExtendedMainAccess dbAccess;
	
	/*
	 * (non-Javadoc)
	 * @see recommender.impl.multiplexer.strategy.SelectFixRecommenderWithFallback#selectResult(java.lang.Long, recommender.impl.multiplexer.RecommendationResultManager, java.util.Collection)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void selectResult(Long qid,
			RecommendationResultManager<E, R> resultCache,
			Collection<R> recommendationResults) {
		super.selectResult(qid, resultCache, recommendationResults);
		
		Iterator<R> it = recommendationResults.iterator();
		final List<Integer> toRetrieve = new ArrayList<Integer>();
		final Map<String, Pair<Double, Double>> saveEvaluation = new HashMap<String, Pair<Double, Double>>();
		while(it.hasNext()) {
			R current = it.next();
			if(!(current instanceof RecommendationPost)) {
				toRetrieve.add(Integer.parseInt(current.getRecommendationId()));
				saveEvaluation.put(current.getRecommendationId(), new Pair<Double, Double>(current.getScore(), current.getConfidence()));
				it.remove();
			}
		}
		
		final List<RecommendationItem> items = dbAccess.getResourcesByIds(toRetrieve);
		for(RecommendationItem item : items) {
			RecommendedItem recommended = new RecommendedItem(item);
			recommended.setScore(saveEvaluation.get(item.getId()).getFirst());
			recommended.setConfidence(saveEvaluation.get(item.getId()).getSecond());
			recommendationResults.add((R) recommended);
		}
		
	}
	
	public void setDbAccess(ExtendedMainAccess dbAccess) {
		this.dbAccess = dbAccess;
	}

}
