package org.bibsonomy.database.managers.chain.statistic.tag.get;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.statistic.StatisticChainElement;
import org.bibsonomy.database.params.StatisticsParam;

/**
 * Returns the number of relations for the current user.
 * 
 * @author Christian Voigtmann
 * @version $Id$
 */
public class GetRelationCountByUser extends StatisticChainElement{

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
