package org.bibsonomy.database.managers.chain.statistic.document;

import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.statistic.StatisticChainElement;
import org.bibsonomy.database.params.StatisticsParam;
import org.bibsonomy.model.statistics.Statistics;
import org.bibsonomy.util.ValidationUtils;

/**
 * get all uploaded layouts
 *
 * @author dzo
 */
public class GetGlobalLayoutDocumentCount extends StatisticChainElement {

	/* (non-Javadoc)
	 * @see org.bibsonomy.database.managers.chain.ChainElement#canHandle(java.lang.Object)
	 */
	@Override
	protected boolean canHandle(StatisticsParam param) {
		return GroupingEntity.ALL.equals(param.getGrouping()) && ValidationUtils.safeContains(param.getFilters(), FilterEntity.LAYOUT_DOCUMENTS);
	}
	
	@Override
	protected Statistics handle(StatisticsParam param, DBSession session) {
		return new Statistics(this.db.getNumberOfLayoutDocuments(param.getFilters(), session));
	}

}
