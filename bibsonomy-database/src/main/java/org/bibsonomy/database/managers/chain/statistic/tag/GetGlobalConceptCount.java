package org.bibsonomy.database.managers.chain.statistic.tag;

import org.bibsonomy.common.enums.ConceptStatus;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.statistic.StatisticChainElement;
import org.bibsonomy.database.params.StatisticsParam;
import org.bibsonomy.model.statistics.Statistics;

/**
 * retrieves the total number of concepts
 *
 * @author dzo
 */
public class GetGlobalConceptCount extends StatisticChainElement {

	@Override
	protected boolean canHandle(StatisticsParam param) {
		return GroupingEntity.ALL.equals(param.getGrouping()) && ConceptStatus.ALL.equals(param.getConceptStatus());
	}

	@Override
	protected Statistics handle(StatisticsParam param, DBSession session) {
		return new Statistics(this.db.getNumberOfConcepts(session));
	}

}
