package org.bibsonomy.recommender.connector.selector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bibsonomy.common.Pair;
import org.bibsonomy.recommender.connector.database.ExtendedMainAccess;
import org.bibsonomy.recommender.connector.model.RecommendationPost;

import recommender.core.interfaces.model.RecommendationItem;
import recommender.core.interfaces.model.RecommendationResult;
import recommender.impl.model.RecommendedItem;
import recommender.impl.multiplexer.RecommendationResultManager;
import recommender.impl.multiplexer.strategy.SelectAll;

/**
 * Extension of {@link SelectAll} to allow greedy loading.
 * 
 * @author lukas
 *
 * @param <E>
 * @param <R>
 */
public class AdaptedSelectAll<E, R extends RecommendationResult> extends SelectAll<E, R> {

	private ExtendedMainAccess dbAccess;
	
	@SuppressWarnings("unchecked")
	@Override
	public void selectResult(Long qid,
			RecommendationResultManager<E, R> resultCache,
			Collection<R> recommendationResults) {
		super.selectResult(qid, resultCache, recommendationResults);
		
		// fetch bibsonomy model classes and wrap them
		Iterator<R> it = recommendationResults.iterator();
		final List<Integer> toRetrieve = new ArrayList<Integer>();
		final Map<String, Pair<Double, Double>> saveEvaluation = new HashMap<String, Pair<Double, Double>>();
		while(it.hasNext()) {
			R current = it.next();
			if(!(current instanceof RecommendationPost)) {
				toRetrieve.add(new Integer(current.getRecommendationId()));
				saveEvaluation.put(current.getRecommendationId(), new Pair<Double, Double>(current.getScore(), current.getConfidence()));
				it.remove();
			}
		}
		
		final List<RecommendationItem> items = dbAccess.getResourcesByIds(toRetrieve);
		for (RecommendationItem item : items) {
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
