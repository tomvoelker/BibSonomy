package org.bibsonomy.database.managers.chain.statistic.post.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.InboxDatabaseManager;
import org.bibsonomy.database.managers.chain.statistic.StatisticChainElement;
import org.bibsonomy.database.params.StatisticsParam;
import org.bibsonomy.database.util.DBSession;



/**
 * Gets count of resources in the inbox of a user
 *  
 */
public class GetResourcesForUserInboxCount extends StatisticChainElement {

	private InboxDatabaseManager inboxDb;
	
	@Override
	protected List<Integer> handle(StatisticsParam param, DBSession session) {
		log.debug("getResourcesForUserInbox");
		inboxDb = InboxDatabaseManager.getInstance();
		List<Integer> counts = new ArrayList<Integer>();
		counts.add(this.inboxDb.getNumInboxMessages(param, session));
		return counts;
	}

	@Override
	protected boolean canHandle(StatisticsParam param) {
		return 	( param.getGrouping() == GroupingEntity.INBOX) && 
				present(param.getRequestedUserName() );
	}
}