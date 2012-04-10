package org.bibsonomy.recommender.tags.multiplexer.strategy;

import java.sql.SQLException;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.recommender.tags.multiplexer.RecommendedTagResultManager;

/**
 * @author fei
 * @version $Id$
 */
public class SelectAll extends SimpleSelector {
	private static final Log log = LogFactory.getLog(SelectAll.class);
	
	/**
	 * Selection strategy which simply selects each recommended tag
	 */
	@Override
	public void selectResult(final Long qid, final RecommendedTagResultManager resultCache, final Collection<RecommendedTag> recommendedTags) throws SQLException {
		log.debug("Selecting result.");
		dbLogic.getRecommendations(qid, recommendedTags);
	}

	@Override
	public String getInfo() {
		return "Strategy for selecting all recommended Tags.";
	}
}
