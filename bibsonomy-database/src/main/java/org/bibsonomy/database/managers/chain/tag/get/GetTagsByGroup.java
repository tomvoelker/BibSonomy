package org.bibsonomy.database.managers.chain.tag.get;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.tag.TagChainElement;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.util.Transaction;
import org.bibsonomy.model.Tag;

/**
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetTagsByGroup extends TagChainElement {

	/**
	 * return a list of tags by a given group. Following arguments have to be
	 * given:
	 * 
	 * grouping:group name:given regex:irrelevant
	 */
	@Override
	protected List<Tag> handle(final TagParam param, final Transaction session) {
		param.setGroupId(this.generalDb.getGroupIdByGroupName(param, session));
		param.setGroups(this.generalDb.getGroupsForUser(param, session));

		return this.db.getTagsByGroup(param, session);
	}

	@Override
	protected boolean canHandle(final TagParam param) {
		return param.getUserName() != null && param.getGrouping() == GroupingEntity.GROUP && param.getRequestedGroupName() != null;
	}
}