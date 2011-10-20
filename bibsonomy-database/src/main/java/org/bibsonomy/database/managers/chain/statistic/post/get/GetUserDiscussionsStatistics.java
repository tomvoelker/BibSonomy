package org.bibsonomy.database.managers.chain.statistic.post.get;

import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.database.managers.chain.statistic.StatisticChainElement;
import org.bibsonomy.database.params.StatisticsParam;
import org.bibsonomy.model.statistics.Statistics;

/**
 * Gets count of resources in the inbox of a user
 *  
 */
public class GetUserDiscussionsStatistics extends StatisticChainElement {

	@Override
	protected Statistics handle(StatisticsParam param, DBSession session) {
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