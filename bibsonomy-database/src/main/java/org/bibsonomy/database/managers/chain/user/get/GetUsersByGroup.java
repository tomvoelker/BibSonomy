package org.bibsonomy.database.managers.chain.user.get;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.user.UserChainElement;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.model.User;

import static org.bibsonomy.util.ValidationUtils.present;

/**
 * Get group members
 * 
 * @author Dominik Benz
 * @version $Id$
 */
public class GetUsersByGroup extends UserChainElement {

	@Override
	protected List<User> handle(final UserParam param, final DBSession session) {
		return this.groupDb.getGroupMembers(param.getUserName(), param.getRequestedGroupName(), session).getUsers();
	}

	@Override
	protected boolean canHandle(final UserParam param) {
		return (GroupingEntity.GROUP.equals(param.getGrouping()) && 
				present(param.getRequestedGroupName()));
	}
}