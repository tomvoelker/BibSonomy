package org.bibsonomy.recommender.connector.testutil;

import java.sql.SQLException;
import java.util.Collection;

import recommender.core.model.RecommendedTag;
import recommender.impl.multiplexer.RecommendedTagResultManager;
import recommender.impl.multiplexer.tags.strategy.SelectAll;

/**
 * @author fei
 * @version $Id$
 */
public class SelectCounter extends SelectAll {
	
	private int recoCounter;

	/**
	 * Selection strategy which simply selects each recommended tag
	 */
	@Override
	public void selectResult(final Long qid, final RecommendedTagResultManager resultCache, final Collection<RecommendedTag> recommendedTags) throws SQLException {
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
