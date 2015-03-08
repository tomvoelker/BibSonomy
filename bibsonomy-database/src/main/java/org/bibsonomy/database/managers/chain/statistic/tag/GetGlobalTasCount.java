package org.bibsonomy.database.managers.chain.statistic.tag;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.common.enums.GroupingEntity;
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
		return GroupingEntity.ALL.equals(param.getGrouping()) && !present(param.getConceptStatus());
	}

	@Override
	protected Statistics handle(StatisticsParam param, DBSession session) {
		return new Statistics(this.db.getNumberOfTas(param.getContentType(), param.getStartDate(), param.getFilters(), session));
	}

}
