package org.bibsonomy.database.managers;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.model.logic.query.Query;
import org.bibsonomy.model.statistics.Statistics;

/**
 * TODO: find a better name
 *
 * @author dzo
 */
public interface StatisticsProvider<Q extends Query> {

	/**
	 * get statistics for the provided query
	 * @param query
	 * @param session
	 * @return
	 */
	public Statistics getStatistics(final Q query, final DBSession session);
}
