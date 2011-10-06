package org.bibsonomy.database.managers.chain.statistic.post.get;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.InboxDatabaseManager;
import org.bibsonomy.database.managers.chain.statistic.StatisticChainElement;
import org.bibsonomy.database.params.StatisticsParam;
import org.bibsonomy.model.statistics.Statistics;

/**
 * Gets count of resources in the inbox of a user
 *  
 */
public class GetResourcesForUserInboxCount extends StatisticChainElement {

	private InboxDatabaseManager inboxDb;
	
	@Override
	protected Statistics handle(StatisticsParam param, DBSession session) {
		this.inboxDb = InboxDatabaseManager.getInstance();
		return new Statistics(this.inboxDb.getNumInboxMessages(param.getRequestedUserName(), param.getContentTypeConstant(), session));
	}

	@Override
	protected boolean canHandle(StatisticsParam param) {
		return 	(param.getGrouping() == GroupingEntity.INBOX) && 
				present(param.getRequestedUserName());
	}
}