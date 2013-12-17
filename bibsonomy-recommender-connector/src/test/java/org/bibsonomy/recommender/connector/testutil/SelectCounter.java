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
