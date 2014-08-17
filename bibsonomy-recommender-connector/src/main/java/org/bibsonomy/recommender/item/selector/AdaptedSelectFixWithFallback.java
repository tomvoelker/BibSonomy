package org.bibsonomy.recommender.item.selector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bibsonomy.common.Pair;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.recommender.item.model.RecommendationUser;
import org.bibsonomy.recommender.item.model.RecommendedPost;
import org.bibsonomy.recommender.item.service.ExtendedMainAccess;

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
public class AdaptedSelectFixWithFallback<R extends Resource> extends SelectFixRecommenderWithFallback<RecommendationUser, RecommendedPost<R>> {
	
	private ExtendedMainAccess<R> dbAccess;
	
	
	/* (non-Javadoc)
	 * @see recommender.impl.multiplexer.strategy.SelectFixRecommenderWithFallback#selectResult(java.lang.Long, recommender.impl.multiplexer.RecommendationResultManager, java.util.Collection)
	 */
	@Override
	public void selectResult(Long qid, RecommendationResultManager<RecommendationUser, RecommendedPost<R>> resultCache, Collection<RecommendedPost<R>> recommendationResults) {
		super.selectResult(qid, resultCache, recommendationResults);
		
		Iterator<RecommendedPost<R>> it = recommendationResults.iterator();
		final List<Integer> toRetrieve = new ArrayList<Integer>();
		final Map<Integer, Pair<Double, Double>> saveEvaluation = new HashMap<Integer, Pair<Double, Double>>();
		while(it.hasNext()) {
			RecommendedPost<R> current = it.next();
			toRetrieve.add(current.getPost().getContentId());
			saveEvaluation.put(current.getPost().getContentId(), new Pair<Double, Double>(current.getScore(), current.getConfidence()));
			it.remove();
		}
		
		final List<Post<R>> items = dbAccess.getResourcesByIds(toRetrieve);
		for(Post<R> item : items) {
			RecommendedPost<R> recommended = new RecommendedPost<R>();
			recommended.setPost(item);
			recommended.setScore(saveEvaluation.get(item.getContentId()).getFirst());
			recommended.setConfidence(saveEvaluation.get(item.getContentId()).getSecond());
			recommendationResults.add(recommended);
		}
	}
	
	public void setDbAccess(ExtendedMainAccess<R> dbAccess) {
		this.dbAccess = dbAccess;
	}

}
