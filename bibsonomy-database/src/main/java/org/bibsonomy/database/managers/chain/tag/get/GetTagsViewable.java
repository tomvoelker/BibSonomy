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
public class GetTagsViewable extends TagChainElement {

	/**
	 * return a list of tags by a logged user. Following arguments have to be
	 * given:
	 * 
	 * grouping:viewable name:given regex: irrelevant
	 */
	@Override
	protected List<Tag> handle(final TagParam param, final Transaction session) {
		log.debug(this.getClass().getSimpleName());

		param.setGroupId(this.generalDb.getGroupIdByGroupName(param, session));
		param.setGroups(this.generalDb.getGroupsForUser(param, session));

		return this.db.getTagsViewable(param, session);
	}

	@Override
	protected boolean canHandle(final TagParam param) {
		return param.getUserName() != null && param.getGrouping() == GroupingEntity.VIEWABLE && param.getRequestedGroupName() != null;
	}
}