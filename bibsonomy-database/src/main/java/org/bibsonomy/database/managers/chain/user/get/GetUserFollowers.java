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
 * Get the list of users which follow the given user.
 * 
 * @author Christian Kramer
 * @version $Id$
 */
public class GetUserFollowers extends UserChainElement{
	
	@Override
	protected List<User> handle(UserParam param, DBSession session) {
		return this.userDB.getUserRelation(param.getUserName(), UserRelation.OF_FOLLOWER, session);
	}

	@Override
	protected boolean canHandle(UserParam param) {
		return (GroupingEntity.FOLLOWER.equals(param.getGrouping()) &&
				present(param.getUserName()) &&
				UserRelation.OF_FOLLOWER.equals(param.getUserRelation()));
	}

}
