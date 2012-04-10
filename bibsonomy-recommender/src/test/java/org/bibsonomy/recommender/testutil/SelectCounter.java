package org.bibsonomy.recommender.testutil;

import java.sql.SQLException;
import java.util.Collection;

import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.recommender.tags.multiplexer.RecommendedTagResultManager;
import org.bibsonomy.recommender.tags.multiplexer.strategy.SelectAll;

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
