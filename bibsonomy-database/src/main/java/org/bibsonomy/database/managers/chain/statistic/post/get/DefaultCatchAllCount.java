package org.bibsonomy.database.managers.chain.statistic.post.get;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.statistic.StatisticChainElement;
import org.bibsonomy.database.params.StatisticsParam;
import org.bibsonomy.model.statistics.Statistics;

/**
 * @author DaiLL
 * @version $Id$
 * 
 * Catches all possibilities of requesting statistics in case of no match without throwing an error.
 */
public class DefaultCatchAllCount extends StatisticChainElement {

	@Override
	protected Statistics handle(StatisticsParam param, DBSession session) {
		return null;
	}
	
	@Override
	protected boolean canHandle(StatisticsParam param) {
		return true;
	}

}
