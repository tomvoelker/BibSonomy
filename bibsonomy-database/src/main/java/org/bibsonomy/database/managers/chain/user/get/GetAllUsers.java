package org.bibsonomy.database.managers.chain.user.get;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.user.UserChainElement;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.model.User;

import static org.bibsonomy.util.ValidationUtils.present;


/**
 * Get all users.
 * 
 * @author Dominik Benz
 * @version $Id$
 */
public class GetAllUsers extends UserChainElement {

	@Override
	protected List<User> handle(final UserParam param, final DBSession session) {
		return this.userDB.getAllUsers(param.getOffset(), param.getLimit(), session);
	}

	@Override
	protected boolean canHandle(final UserParam param) {
		return (GroupingEntity.ALL.equals(param.getGrouping()) &&
				!present(param.getTagIndex()));
	}
}