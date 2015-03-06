package org.bibsonomy.database.managers.chain.statistic.tag;

import org.bibsonomy.common.enums.ConceptStatus;
import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.statistic.StatisticChainElement;
import org.bibsonomy.database.params.StatisticsParam;
import org.bibsonomy.model.statistics.Statistics;

/**
 * retrieves the number of concepts in the log table
 *
 * @author dzo
 */
public class GetGlobalConceptHistoryCount extends StatisticChainElement {

	@Override
	protected boolean canHandle(StatisticsParam param) {
		return GroupingEntity.ALL.equals(param.getGrouping()) && ConceptStatus.ALL.equals(param.getConceptStatus()) && FilterEntity.HISTORY.equals(param.getFilter());
	}

	@Override
	protected Statistics handle(StatisticsParam param, DBSession session) {
		return new Statistics(this.db.getNumberOfConceptsInHistory(session));
	}

}
