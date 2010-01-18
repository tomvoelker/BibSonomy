package org.bibsonomy.database.managers.chain.statistic.post.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.statistic.StatisticChainElement;
import org.bibsonomy.database.params.StatisticsParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;

/**
 * Counts of resources within a group
 * 
 * @author Stefan Stützer
 * @version $Id$
 */
public class GetResourcesForGroupCount extends StatisticChainElement {

	@Override
	protected List<Integer> handle(StatisticsParam param, DBSession session) {
		List<Integer> counts = new ArrayList<Integer>();
		
		final Group group = this.groupDb.getGroupByName(param.getRequestedGroupName(), session);
		if (group == null || group.getGroupId() == GroupID.INVALID.getId() || GroupID.isSpecialGroupId(group.getGroupId())) {
			log.debug("group " + param.getRequestedGroupName() + " not found or special group");
			return new ArrayList<Integer>(Arrays.asList(0));			
		}
		if (param.getContentType() == ConstantID.BIBTEX_CONTENT_TYPE.getId()) {
			counts.add(this.db.getNumberOfResourcesForGroup(BibTex.class, param.getRequestedUserName(), param.getUserName(), group.getGroupId(), param.getGroups(), session));
		} else if (param.getContentType() == ConstantID.BOOKMARK_CONTENT_TYPE.getId()) {
			counts.add(this.db.getNumberOfResourcesForGroup(Bookmark.class, param.getRequestedUserName(), param.getUserName(), group.getGroupId(), param.getGroups(), session));
		}
		return counts;
	}

	@Override
	protected boolean canHandle(StatisticsParam param) {
		return 	param.getGrouping() == GroupingEntity.GROUP && 
				present(param.getRequestedGroupName());
	}
}