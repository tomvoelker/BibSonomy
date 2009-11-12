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
 * This chain element gets all follower of the logged in user.
 * 
 * @author Christian Kramer
 * @version $Id$
 */
public class GetFollowersOfUser extends UserChainElement{

	@Override
	protected List<User> handle(UserParam param, DBSession session) {
		return this.userDB.getUserRelation(param.getUserName(), UserRelation.FOLLOWER_OF, session);
	}
	
	@Override
	protected boolean canHandle(UserParam param) {
		return (GroupingEntity.FOLLOWER.equals(param.getGrouping()) &&
				present(param.getUserName()) &&
				UserRelation.FOLLOWER_OF.equals(param.getUserRelation()));
	}

}
