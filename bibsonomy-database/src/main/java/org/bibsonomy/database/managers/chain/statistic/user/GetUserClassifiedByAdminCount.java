package org.bibsonomy.database.managers.chain.statistic.user;

import org.bibsonomy.common.enums.Classifier;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.statistic.StatisticChainElement;
import org.bibsonomy.database.params.StatisticsParam;
import org.bibsonomy.model.statistics.Statistics;

/**
 * @author dzo
 */
public class GetUserClassifiedByAdminCount extends StatisticChainElement {

	/* (non-Javadoc)
	 * @see org.bibsonomy.database.managers.chain.ChainElement#handle(java.lang.Object, org.bibsonomy.database.common.DBSession)
	 */
	@Override
	protected Statistics handle(StatisticsParam param, DBSession session) {
		return new Statistics(this.db.getNumberOfClassifiedUsersByAdmin(param.getSpamStatus(), param.getInterval(), session));
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.database.managers.chain.ChainElement#canHandle(java.lang.Object)
	 */
	@Override
	protected boolean canHandle(StatisticsParam param) {
		return GroupingEntity.ALL.equals(param.getGrouping()) && Classifier.ADMIN.equals(param.getClassifier());
	}

}
