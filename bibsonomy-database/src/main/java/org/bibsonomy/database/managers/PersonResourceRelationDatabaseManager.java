package org.bibsonomy.database.managers;

import org.bibsonomy.database.common.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.Chain;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.query.ResourcePersonRelationQuery;
import org.bibsonomy.model.statistics.Statistics;

/**
 * database manager for person resource relations
 *
 * @author dzo
 */
public class PersonResourceRelationDatabaseManager extends AbstractDatabaseManager implements StatisticsProvider<ResourcePersonRelationQuery> {

	private Chain<Statistics, ResourcePersonRelationQuery> statisticsChain;

	@Override
	public Statistics getStatistics(final ResourcePersonRelationQuery query, final User loggedinUser, final DBSession session) {
		return this.statisticsChain.perform(query, session);
	}

	/**
	 * @param statisticsChain the statisticsChain to set
	 */
	public void setStatisticsChain(final Chain<Statistics, ResourcePersonRelationQuery> statisticsChain) {
		this.statisticsChain = statisticsChain;
	}
}
