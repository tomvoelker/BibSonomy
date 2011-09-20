package org.bibsonomy.database.managers.chain.statistic.post.get;

import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.database.managers.chain.statistic.StatisticChainElement;
import org.bibsonomy.database.params.StatisticsParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;

/**
 * Gets count of resources of a special user
 * 
 * @author Stefan Stützer
 * @version $Id$
 */
public class GetResourcesWithDiscussionsCount extends StatisticChainElement {

	@Override
	protected Integer handle(StatisticsParam param, DBSession session) {
		if (param.getContentType() == ConstantID.BIBTEX_CONTENT_TYPE.getId()) {
			return this.db.getNumberOfResourcesWithDiscussions(BibTex.class, param.getRequestedUserName(), param.getUserName(), param.getGroupId(), param.getGroups(), session);
		}
		
		if (param.getContentType() == ConstantID.BOOKMARK_CONTENT_TYPE.getId()) {
			return this.db.getNumberOfResourcesWithDiscussions(Bookmark.class, param.getRequestedUserName(), param.getUserName(), param.getGroupId(), param.getGroups(), session);
		}
		
		return Integer.valueOf(0);
	}

	@Override
	protected boolean canHandle(StatisticsParam param) {
		return (FilterEntity.POSTS_WITH_DISCUSSIONS.equals(param.getFilter()));
	}
}