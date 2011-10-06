package org.bibsonomy.database.managers.chain.statistic.post.get;

import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.StatisticsDatabaseManager;
import org.bibsonomy.database.managers.chain.statistic.StatisticChainElement;
import org.bibsonomy.database.params.StatisticsParam;
import org.bibsonomy.model.statistics.Statistics;

/**
 * Gets count of resources in the inbox of a user
 *  
 */
public class getUserDiscussionsStatistics extends StatisticChainElement {

	private StatisticsDatabaseManager statDB;
	
	@Override
	protected Statistics handle(StatisticsParam param, DBSession session) {
		this.statDB = StatisticsDatabaseManager.getInstance();
		return this.statDB.getUserDiscussionsStatistics(param, session);
	}

	@Override
	protected boolean canHandle(StatisticsParam param) {
		return 	( FilterEntity.STATISTICS_DISCUSSIONS.equals(param.getFilter()) );
	}
	

}