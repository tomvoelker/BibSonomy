/**
 * BibSonomy-Recommendation-Connector - Connector for the recommender framework for tag and resource recommendation
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.recommender.connector.selector;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bibsonomy.common.Pair;
import org.bibsonomy.recommender.connector.database.ExtendedMainAccess;
import org.bibsonomy.recommender.connector.model.RecommendationPost;

import recommender.core.interfaces.model.RecommendationEntity;
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
public class AdaptedSelectFixWithFallback<E extends RecommendationEntity, R extends RecommendationResult> extends SelectFixRecommenderWithFallback<E, R> {
	
	private ExtendedMainAccess dbAccess;
	
	/*
	 * (non-Javadoc)
	 * @see recommender.impl.multiplexer.strategy.SelectFixRecommenderWithFallback#selectResult(java.lang.Long, recommender.impl.multiplexer.RecommendationResultManager, java.util.Collection)
	 */
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
			if(!(current instanceof RecommendationPost)) {
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
	
	public void setDbAccess(ExtendedMainAccess dbAccess) {
		this.dbAccess = dbAccess;
	}

}
