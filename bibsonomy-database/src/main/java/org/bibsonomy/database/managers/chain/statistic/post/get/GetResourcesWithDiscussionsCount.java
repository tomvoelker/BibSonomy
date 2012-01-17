package org.bibsonomy.database.managers.chain.statistic.post.get;

import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.database.managers.chain.statistic.StatisticChainElement;
import org.bibsonomy.database.params.StatisticsParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.statistics.Statistics;

/**
 * Gets count of resources of a special user
 * 
 * @author Stefan Stützer
 * @version $Id$
 */
public class GetResourcesWithDiscussionsCount extends StatisticChainElement {

	@Override
	protected Statistics handle(StatisticsParam param, DBSession session) {
		if (GroupingEntity.GROUP.equals(param.getGrouping())) {
			final Group group = this.groupDb.getGroupByName(param.getRequestedGroupName(), session);
			if (group == null || group.getGroupId() == GroupID.INVALID.getId() || GroupID.isSpecialGroupId(group.getGroupId())) {
				log.debug("group " + param.getRequestedGroupName() + " not found or special group");
				return new Statistics(0);			
			}
			if (param.getContentType() == ConstantID.BIBTEX_CONTENT_TYPE.getId()) {
				return new Statistics(this.db.getNumberOfResourcesWithDiscussionsForGroup(BibTex.class, group.getGroupId(), param.getUserName(), param.getGroups(), session));
			}
			
			if (param.getContentType() == ConstantID.BOOKMARK_CONTENT_TYPE.getId()) {
				return new Statistics(this.db.getNumberOfResourcesWithDiscussionsForGroup(Bookmark.class, group.getGroupId(), param.getUserName(), param.getGroups(), session));
			}
		}

		// all other (USER and ALL)
		if (param.getContentType() == ConstantID.BIBTEX_CONTENT_TYPE.getId()) {
			return new Statistics(this.db.getNumberOfResourcesWithDiscussions(BibTex.class, param.getRequestedUserName(), param.getUserName(), param.getGroups(), session));
		}

		if (param.getContentType() == ConstantID.BOOKMARK_CONTENT_TYPE.getId()) {
			return new Statistics(this.db.getNumberOfResourcesWithDiscussions(Bookmark.class, param.getRequestedUserName(), param.getUserName(), param.getGroups(), session));
		}
		
		return new Statistics(0);
	}

	@Override
	protected boolean canHandle(StatisticsParam param) {
		return (FilterEntity.POSTS_WITH_DISCUSSIONS.equals(param.getFilter()));
	}
}