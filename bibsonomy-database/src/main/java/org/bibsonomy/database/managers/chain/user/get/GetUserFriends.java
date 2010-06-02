package org.bibsonomy.database.managers.chain.user.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.user.UserChainElement;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.model.User;

/**
 * Get the list of users which have the given user as their friend.
 * 
 * @author Dominik Benz
 * @version $Id$
 */
public class GetUserFriends extends UserChainElement {

	@Override
	protected List<User> handle(final UserParam param, final DBSession session) {
		return this.userDB.getUserRelation(param.getUserName(), UserRelation.FRIEND_OF, session);
	}

	@Override
	protected boolean canHandle(final UserParam param) {
		return (GroupingEntity.FRIEND.equals(param.getGrouping()) &&
				present(param.getUserName()) &&
				UserRelation.OF_FRIEND.equals(param.getUserRelation()));
	}
}