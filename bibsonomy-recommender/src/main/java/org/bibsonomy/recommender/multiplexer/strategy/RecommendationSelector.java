package org.bibsonomy.recommender.multiplexer.strategy;

import java.sql.SQLException;
import java.util.SortedSet;

import org.bibsonomy.model.RecommendedTag;

/**
 * @author fei
 * @version $Id$
 */
public interface RecommendationSelector {
	/**
	 * Selects recommendations for given query
	 * 
	 * @param qid
	 * @return
	 * @throws Exception
	 */
	public SortedSet<RecommendedTag> selectResult(Long qid) throws SQLException;
}
