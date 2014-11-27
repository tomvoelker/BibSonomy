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
import recommender.impl.multiplexer.strategy.SelectAll;

/**
 * Extension of {@link SelectAll} to allow greedy loading.
 * 
 * @author lukas
 *
 * @param <E>
 * @param <R>
 */
public class AdaptedSelectAll<R extends Resource> extends SelectAll<RecommendationUser, RecommendedPost<R>> {

	private ExtendedMainAccess<R> dbAccess;
	
	/* (non-Javadoc)
	 * @see recommender.impl.multiplexer.strategy.SelectAll#selectResult(java.lang.Long, recommender.impl.multiplexer.RecommendationResultManager, java.util.Collection)
	 */
	@Override
	public void selectResult(Long qid, RecommendationResultManager<RecommendationUser, RecommendedPost<R>> resultCache, Collection<RecommendedPost<R>> recommendationResults) {
		super.selectResult(qid, resultCache, recommendationResults);
		
		// fetch bibsonomy model classes and wrap them
		final Iterator<RecommendedPost<R>> it = recommendationResults.iterator();
		final List<Integer> toRetrieve = new ArrayList<Integer>();
		final Map<String, Pair<Double, Double>> saveEvaluation = new HashMap<String, Pair<Double, Double>>();
		while (it.hasNext()) {
			final RecommendedPost<R> current = it.next();
			toRetrieve.add(new Integer(current.getRecommendationId()));
			saveEvaluation.put(current.getRecommendationId(), new Pair<Double, Double>(current.getScore(), current.getConfidence()));
			it.remove();
		}
		// FIXME: save the posts in the framework maybe a remote recommender sends
		final List<Post<R>> items = dbAccess.getResourcesByIds(toRetrieve);
		for (final Post<R> post : items) {
			final RecommendedPost<R> recommended = new RecommendedPost<R>();
			recommended.setPost(post);
			final Pair<Double, Double> scoreAndConfidence = saveEvaluation.get(recommended.getRecommendationId());
			recommended.setScore(scoreAndConfidence.getFirst().doubleValue());
			recommended.setConfidence(scoreAndConfidence.getSecond().doubleValue());
			recommendationResults.add(recommended);
		}
	}
	
	/**
	 * @param dbAccess	the dbAccess to set
	 */
	public void setDbAccess(ExtendedMainAccess<R> dbAccess) {
		this.dbAccess = dbAccess;
	}
}
