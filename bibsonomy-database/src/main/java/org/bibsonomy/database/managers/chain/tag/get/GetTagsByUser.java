package org.bibsonomy.database.managers.chain.tag.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.tag.TagChainElement;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.Tag;

/**
 * @author Dominik Benz
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetTagsByUser extends TagChainElement {

	/**
	 * return a list of tags by a logged user
	 */
	@Override
	protected List<Tag> handle(final TagParam param, final DBSession session) {
		// TODO: is this needed?  param.setGroups(this.generalDb.getGroupsForUser(param, session));
		return this.db.getTagsByUser(param, session);
	}

	@Override
	protected boolean canHandle(final TagParam param) {
		return present(param.getUserName()) && param.getGrouping() == GroupingEntity.USER && present(param.getRequestedGroupName());
	}
}