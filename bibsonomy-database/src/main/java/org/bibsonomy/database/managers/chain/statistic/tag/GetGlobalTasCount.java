package org.bibsonomy.database.managers.chain.statistic.tag;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;
import java.util.Set;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.StatisticsConstraint;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.statistic.StatisticChainElement;
import org.bibsonomy.database.params.StatisticsParam;
import org.bibsonomy.model.statistics.Statistics;

/**
 * count all tas
 *
 * @author dzo
 */
public class GetGlobalTasCount extends StatisticChainElement {

	@Override
	protected boolean canHandle(StatisticsParam param) {
		final Set<StatisticsConstraint> constraints = param.getConstraints();
		return GroupingEntity.ALL.equals(param.getGrouping()) && (!present(constraints) || !constraints.contains(StatisticsConstraint.UNIQUE));
	}

	@Override
	protected Statistics handle(StatisticsParam param, DBSession session) {
		final List<String> usersToExclude = getUsersToExclude(param);
		return new Statistics(this.db.getNumberOfTas(param.getContentType(), param.getStartDate(), usersToExclude, session));
	}

}
