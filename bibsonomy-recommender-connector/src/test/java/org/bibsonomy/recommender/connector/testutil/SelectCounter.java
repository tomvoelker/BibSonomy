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
package org.bibsonomy.recommender.connector.testutil;

import java.sql.SQLException;
import java.util.Collection;

import recommender.core.interfaces.model.RecommendationEntity;
import recommender.core.interfaces.model.RecommendationResult;
import recommender.impl.multiplexer.RecommendationResultManager;
import recommender.impl.multiplexer.strategy.SelectAll;


/**
 * @author fei
 */
public class SelectCounter<E extends RecommendationEntity, R extends RecommendationResult> extends SelectAll<E, R> {
	
	private int recoCounter;

	/**
	 * Selection strategy which simply selects each recommended tag
	 */
	@Override
	public void selectResult(final Long qid, final RecommendationResultManager<E, R> resultCache, final Collection<R> recommendedTags) throws SQLException {
		super.selectResult(qid, resultCache, recommendedTags);
		this.recoCounter = dbLogic.getActiveRecommenderIDs(qid).size();
	}

	/**
	 * @return the recoCounter
	 */
	public int getRecoCounter() {
		return this.recoCounter;
	}
}
