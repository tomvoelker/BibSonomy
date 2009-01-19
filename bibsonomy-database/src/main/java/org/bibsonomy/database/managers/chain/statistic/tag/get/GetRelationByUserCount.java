package org.bibsonomy.database.managers.chain.statistic.tag.get;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.database.managers.chain.statistic.StatisticChainElement;
import org.bibsonomy.database.params.StatisticsParam;
import org.bibsonomy.database.util.DBSession;

/**
 * @author Christian
 * @version $Id$
 */
public class GetRelationByUserCount extends StatisticChainElement{

	@Override
	protected boolean canHandle(StatisticsParam param) {
		
		return true;
	}

	@Override
	protected List<Integer> handle(StatisticsParam param, DBSession session) {
		List<Integer> counts = new ArrayList<Integer>();
		counts.add(this.db.getNumberOfRelationsForUser(param, session));
		
		return counts;
	}

}
