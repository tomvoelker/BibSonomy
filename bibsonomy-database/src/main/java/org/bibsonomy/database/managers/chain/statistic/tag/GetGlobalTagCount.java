package org.bibsonomy.database.managers.chain.statistic.tag;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Set;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.StatisticsConstraint;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.statistic.StatisticChainElement;
import org.bibsonomy.database.params.StatisticsParam;
import org.bibsonomy.model.statistics.Statistics;

/**
 * chain element to count all tags
 *
 * @author dzo
 */
public class GetGlobalTagCount extends StatisticChainElement {

	@Override
	protected boolean canHandle(StatisticsParam param) {
		final Set<StatisticsConstraint> constraints = param.getConstraints();
		return GroupingEntity.ALL.equals(param.getGrouping()) && !present(param.getConceptStatus()) && present(constraints) && constraints.contains(StatisticsConstraint.UNIQUE);
	}

	@Override
	protected Statistics handle(StatisticsParam param, DBSession session) {
		return new Statistics(this.db.getNumberOfTags(session));
	}

}