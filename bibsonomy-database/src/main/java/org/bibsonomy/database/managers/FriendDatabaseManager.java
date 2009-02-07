package org.bibsonomy.database.managers;

import java.util.List;

import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.database.util.LogicInterfaceHelper;
import org.bibsonomy.model.User;

/**
 * @author Steffen
 * @version $Id$
 */
public class FriendDatabaseManager extends AbstractDatabaseManager {
	
	private static FriendDatabaseManager singleton = null;
	
	private FriendDatabaseManager() {}

	/**
	 * @return singleton
	 */
	public static FriendDatabaseManager getInstance() {
		if(singleton==null) {
			singleton = new FriendDatabaseManager();
		}
		return singleton;
	}

	/**
	 * @param loginUser
	 * @param param
	 * @param session
	 * @return list of users
	 */
	public List<User> getUserFriends(User loginUser, final UserParam param, DBSession session) {
		return this.queryForList("getUserFriends", param, User.class, session);
	}

	/**
	 * @param loginUser
	 * @param param
	 * @param session
	 * @return list of users
	 */
	public List<User> getFriendsOfUser(User loginUser, final UserParam param, DBSession session) {
		return this.queryForList("getFriendsOfUser", param, User.class, session);
	}
}
