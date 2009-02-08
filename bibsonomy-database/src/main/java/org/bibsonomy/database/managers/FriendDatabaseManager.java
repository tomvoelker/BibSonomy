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
	 * @param user 
	 * @param param
	 * @param session
	 * @return list of users
	 */
	public List<User> getUserFriends(User user, DBSession session) {
		return this.queryForList("getUserFriends", user.getName().toLowerCase(), User.class, session);
	}

	/**
	 * @param user
	 * @param session
	 * @return list of users
	 */
	public List<User> getFriendsOfUser(User user, DBSession session) {
		return this.queryForList("getFriendsOfUserByStringAsStrings", user.getName().toLowerCase(), User.class, session);
	}
}
