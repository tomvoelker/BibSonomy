package org.bibsonomy.database.managers.chain.user.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.user.UserChainElement;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.model.User;

/**
 * Get users by Search-String
 * 
 * @author bsc
 * @version $Id$
 */
public class GetUsersBySearch extends UserChainElement {

	@Override
	protected List<User> handle(final UserParam param, final DBSession session) {
		return this.userDB.getUsersBySearch(param.getSearch(), param.getLimit(), session);
	}

	@Override
	protected boolean canHandle(final UserParam param) {
		return (GroupingEntity.USER.equals(param.getGrouping()) &&
				present(param.getSearch()) &&
				present(param.getLimit()));
	}
}