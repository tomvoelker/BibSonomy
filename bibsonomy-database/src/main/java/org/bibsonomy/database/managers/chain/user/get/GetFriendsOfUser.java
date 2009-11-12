package org.bibsonomy.database.managers.chain.user.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.database.managers.chain.user.UserChainElement;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.User;

/**
 * Get friends of the logged-in user (i.e. all users u_f which are a friend of the logged-in user u) 
 * 
 * @author Dominik Benz
 * @version $Id$
 */
public class GetFriendsOfUser extends UserChainElement {

	@Override
	protected List<User> handle(final UserParam param, final DBSession session) {
		return this.userDB.getUserRelation(param.getUserName(), UserRelation.OF_FRIEND, session);
	}

	@Override
	protected boolean canHandle(final UserParam param) {
		return (GroupingEntity.FRIEND.equals(param.getGrouping()) &&
				present(param.getUserName()) &&
				UserRelation.FRIEND_OF.equals(param.getUserRelation()));
	}
}