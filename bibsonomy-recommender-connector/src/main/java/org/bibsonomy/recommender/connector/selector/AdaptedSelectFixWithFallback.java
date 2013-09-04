package org.bibsonomy.recommender.connector.selector;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bibsonomy.common.Pair;
import org.bibsonomy.recommender.connector.database.RecommenderDBLogic;
import org.bibsonomy.recommender.connector.model.PostWrapper;
import org.bibsonomy.recommender.connector.model.RecommendedPost;

import recommender.core.interfaces.database.RecommenderDBAccess;
import recommender.core.interfaces.model.RecommendationEntity;
import recommender.core.interfaces.model.RecommendationItem;
import recommender.core.interfaces.model.RecommendationResult;
import recommender.core.interfaces.model.RecommendedItem;
import recommender.impl.multiplexer.RecommendationResultManager;
import recommender.impl.multiplexer.tags.strategy.SelectFixRecommenderWithFallback;

public class AdaptedSelectFixWithFallback<E extends RecommendationEntity, R extends RecommendationResult> extends SelectFixRecommenderWithFallback<E, R> {
	
	private RecommenderDBLogic dbAccess;
	
	@SuppressWarnings("unchecked")
	@Override
	public void selectResult(Long qid,
			RecommendationResultManager<E, R> resultCache,
			Collection<R> recommendationResults) throws SQLException {
		super.selectResult(qid, resultCache, recommendationResults);
		
		Iterator<R> it = recommendationResults.iterator();
		final List<Integer> toRetrieve = new ArrayList<Integer>();
		final Map<String, Pair<Double, Double>> saveEvaluation = new HashMap<String, Pair<Double, Double>>();
		while(it.hasNext()) {
			R current = it.next();
			if(!(current instanceof PostWrapper)) {
				toRetrieve.add(Integer.parseInt(current.getId()));
				saveEvaluation.put(current.getId(), new Pair<Double, Double>(current.getScore(), current.getConfidence()));
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
	
	public void setDbAccess(RecommenderDBLogic dbAccess) {
		this.dbAccess = dbAccess;
	}

}
