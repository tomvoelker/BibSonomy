package org.bibsonomy.database.managers.chain.statistic.post;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.statistic.StatisticChainElement;
import org.bibsonomy.database.params.StatisticsParam;
import org.bibsonomy.model.statistics.Statistics;

/**
 * @author dzo
 */
public class GetClipboardPostsCount extends StatisticChainElement {

	@Override
	protected Statistics handle(StatisticsParam param, DBSession session) {
		return new Statistics(this.db.getNumberOfClipboardPosts(session));
	}
	
	@Override
	protected boolean canHandle(StatisticsParam param) {
		return GroupingEntity.CLIPBOARD.equals(param.getGrouping()) && !present(param.getRequestedUserName());
	}
}
