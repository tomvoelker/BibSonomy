package org.bibsonomy.database.managers.chain.statistic.post.get;

import java.util.List;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.statistic.StatisticChainElement;
import org.bibsonomy.database.params.StatisticsParam;

/**
 * @author DaiLL
 * @version $Id$
 * 
 * Catches all possibilities of requesting statistics in case of no match without throwing an error.
 */
public class DefaultCatchAllCount extends StatisticChainElement{

	@Override
	protected List<Integer> handle(StatisticsParam param, DBSession session) {
		return null;
	}
	
	@Override
	protected boolean canHandle(StatisticsParam param) {
		return true;
	}

}
