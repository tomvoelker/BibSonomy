package org.bibsonomy.database.managers;

import java.util.List;

import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.model.User;
/**
 * Used to retrieve users from the database.
 * @author mgr
 */
public class UserDatabaseManager extends AbstractDatabaseManager  {

	/** Singleton */
	private  final static UserDatabaseManager singleton = new UserDatabaseManager();
	private final GeneralDatabaseManager generalDb;

	UserDatabaseManager() {
		this.generalDb = GeneralDatabaseManager.getInstance();
	}

	public static UserDatabaseManager getInstance() {
		return singleton;
	}

	@SuppressWarnings("unchecked")
	protected List<User> userList(final String query, final User user) {
		return (List<User>) queryForList(query, user);
	}

	public List<User> getAllUsersOfBibSonomy(User user) {
		return this.userList("getAllUsersOfBibSonomy", user);
	}
	
	/*
	 * get all Users of a given Group 
	 */
	
	public List<User> getUsersOfGroup(final User user) {
		return this.userList("getUsersOfGroup", user);
	}
	
	/*
	 * get details by a given group of a user
	 */
	
	public User getUserDetails(final User user) {
		return (User) this.queryForObject("getUserDetails", user);
	}
	
	
	/*
	 * request:get all Users of BibSonomy, which are currently logged
	 */
	
	public List<User> getUsers(String authUser, int start, int end) {
		return null;
	}

	/*
	 * returns all users who are members of the specified group
	 */
	
	public List<User> getUsers(String authUser, String groupName, int start, int end) {
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
		return true;
	}

	
	
}