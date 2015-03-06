package org.bibsonomy.database.managers.chain.statistic.user;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Set;

import org.bibsonomy.common.enums.StatisticsConstraint;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.statistic.StatisticChainElement;
import org.bibsonomy.database.params.StatisticsParam;
import org.bibsonomy.model.statistics.Statistics;

/**
 * @author dzo
 */
public class GetActiveUserCount extends StatisticChainElement {
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.database.managers.chain.ChainElement#canHandle(java.lang.Object)
	 */
	@Override
	protected boolean canHandle(StatisticsParam param) {
		final Set<StatisticsConstraint> constraints = param.getConstraints();
		return present(constraints) && constraints.contains(StatisticsConstraint.ACTIVE);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.database.managers.chain.ChainElement#handle(java.lang.Object, org.bibsonomy.database.common.DBSession)
	 */
	@Override
	protected Statistics handle(StatisticsParam param, DBSession session) {
		return new Statistics(db.getNumberOfActiveUsers(param.getInterval(), param.getUnit(), session));
	}
}