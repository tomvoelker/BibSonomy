package org.bibsonomy.recommender.multiplexer.strategy;

import java.sql.SQLException;
import java.util.SortedSet;

import org.apache.log4j.Logger;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.recommender.DBAccess;
import org.bibsonomy.recommender.multiplexer.MultiplexingTagRecommender;

/**
 * @author fei
 * @version $Id$
 */
public class SelectAll implements RecommendationSelector {
	private static final Logger log = Logger.getLogger(SelectAll.class);
	
	/**
	 * Selection strategy which simply selects each recommended tag
	 */
	public SortedSet<RecommendedTag> selectResult(Long qid) throws SQLException {
		log.debug("Selecting result.");
		final SortedSet<RecommendedTag> result = DBAccess.getRecommendations(qid);
		return result;
	}

}
