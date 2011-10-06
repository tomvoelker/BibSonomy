package org.bibsonomy.database.managers.chain.statistic.tag.get;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.statistic.StatisticChainElement;
import org.bibsonomy.database.params.StatisticsParam;
import org.bibsonomy.model.statistics.Statistics;

/**
 * Returns the number of relations for the current user.
 * 
 * @author Christian Voigtmann
 * @version $Id$
 */
public class GetRelationCountByUser extends StatisticChainElement {

	@Override
	protected boolean canHandle(StatisticsParam param) {
		return true;
	}

	@Override
	protected Statistics handle(StatisticsParam param, DBSession session) {
		return new Statistics(this.db.getNumberOfRelationsForUser(param, session));
	}

}
