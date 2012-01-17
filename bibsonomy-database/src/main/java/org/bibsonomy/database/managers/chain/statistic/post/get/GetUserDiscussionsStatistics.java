package org.bibsonomy.database.managers.chain.statistic.post.get;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.database.managers.chain.statistic.StatisticChainElement;
import org.bibsonomy.database.params.StatisticsParam;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.statistics.Statistics;

/**
 * Gets count of resources in the inbox of a user
 *  
 */
public class GetUserDiscussionsStatistics extends StatisticChainElement {

	@Override
	protected Statistics handle(StatisticsParam param, DBSession session) {
		if (GroupingEntity.GROUP.equals(param.getGrouping())) {
			final Group group = this.groupDb.getGroupByName(param.getRequestedGroupName(), session);
			if (!present(group) || group.getGroupId() == GroupID.INVALID.getId() || GroupID.isSpecialGroupId(group.getGroupId())) {
				log.debug("group '" + param.getRequestedGroupName() + "' not found or special group");
				return new Statistics(0);			
			}
			param.setGroupId(group.getGroupId());
			return this.db.getUserDiscussionsStatisticsForGroup(param, session);
		}
		// all other (USER and ALL)
		return this.db.getUserDiscussionsStatistics(param, session);
	}

	@Override
	protected boolean canHandle(StatisticsParam param) {
		return 	( 
					FilterEntity.POSTS_WITH_DISCUSSIONS.equals(param.getFilter()) &&
					ConstantID.ALL_CONTENT_TYPE.equals(param.getContentTypeConstant())
				);
	}
	

}