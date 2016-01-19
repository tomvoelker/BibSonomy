/**
 * BibSonomy-Recommendation-Connector - Connector for the recommender framework for tag and resource recommendation
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
import recommender.impl.multiplexer.strategy.SelectAll;

/**
 * Extension of {@link SelectAll} to allow greedy loading.
 * 
 * @author lukas
 *
 * @param <E>
 * @param <R>
 */
public class AdaptedSelectAll<E extends RecommendationEntity, R extends RecommendationResult> extends SelectAll<E, R> {

	private ExtendedMainAccess dbAccess;
	
	@SuppressWarnings("unchecked")
	@Override
	public void selectResult(Long qid,
			RecommendationResultManager<E, R> resultCache,
			Collection<R> recommendationResults) throws SQLException {
		super.selectResult(qid, resultCache, recommendationResults);
		
		// fetch bibsonomy model classes and wrap them
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
