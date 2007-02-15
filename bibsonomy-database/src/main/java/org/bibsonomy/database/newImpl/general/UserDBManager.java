package org.bibsonomy.database.newImpl.general;

import java.util.Set;

import org.bibsonomy.model.User;

/*
 * TODO: this class implements the user specific queries of the LogicInterface
 */

public class UserDBManager {

	public Set<User> getUsers(String authUser, int start, int end) {
		return null;
	}

	public Set<User> getUsers(String authUser, String groupName, int start, int end) {
		return null;
	}
	
	public User getUserDetails(String authUserName, String userName) {
		return null;
	}
	
	public void deleteUser(String userName) {

	}
	
	public void storeUser(User user, boolean update) {

	}
	
	public boolean validateUserAccess(String username, String password) {
		return false;
	}
}
