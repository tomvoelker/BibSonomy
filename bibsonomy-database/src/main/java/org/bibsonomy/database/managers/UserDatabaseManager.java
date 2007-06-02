package org.bibsonomy.database.managers;

import java.util.List;

import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.database.util.Transaction;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.UserUtils;
/**
 * Used to retrieve users from the database.
 *
 * @author Miranda Grahl
 * @version $Id$
 */
public class UserDatabaseManager extends AbstractDatabaseManager  {

	/** Singleton */
	private  final static UserDatabaseManager singleton = new UserDatabaseManager();

	private UserDatabaseManager() {
	}

	public static UserDatabaseManager getInstance() {
		return singleton;
	}

	/**
	 * Get Api key for given user.
	 * 
	 * @param user
	 * @param session
	 * @return
	 */
	public String getApiKeyForUser(final UserParam user, final Transaction session) {
		return this.queryForObject("getApiKeyForUser", user, String.class, session);
	}

	/**
	 * Generate an API key for an existing user.
	 * 
	 * @param user
	 * @param session
	 */	
	public void updateApiKeyForUser(final UserParam user, final Transaction session) {
		user.setApiKey(UserUtils.generateApiKey());
		this.update("updateApiKeyForUser", user, session);
	}

	/**
	 * @param session
	 */
	// XXX: do we really want to do this? i.e. allow all users to access the api.
	public void generateApiKeysForAllUsers(final Transaction session) {
		final UserParam param = new UserParam();
		final List<User> allUsers = this.getAllUsersWithoutApiKey(param, session);

		for (final User user : allUsers) {
			log.debug(user);
			param.setUserName(user.getName());
			param.setApiKey(UserUtils.generateApiKey());
			updateApiKeyForUser(param, session);
		}
		log.debug("Finished with " + allUsers.size() + " users");
	}

	/*
	 * TODO: renewApiKey
	 */

	/*
	 * XXX: get all Users of a given Group required different view right
	 */

	public List<String> getUserNamesByGroupId(final Integer groupId, final Transaction session) {
		return this.queryForList("getUserNamesByGroupId", groupId, String.class, session);
	}

	public List<User> getUsersOfGroupPublic(final UserParam user, final Transaction session) {
		return null; // TODO: implement
	}

	public List<User> getUsersOfGroupPrivate(final UserParam user, final Transaction session) {
		return this.queryForList("getUsersOfGroupPrivate", user, User.class, session);
	}

	public List<User> getUsersOfGroupFriends(final UserParam user, final Transaction session) {
		return null; // TODO: implement
	}

	public Integer getPrivlevelOfUser(final UserParam user, final Transaction session) {
		return this.queryForObject("getPrivlevelOfUser", user, Integer.class, session);
	}

	/**
	 * Get details for a given user
	 */
	public User getUserDetails(final UserParam user, final Transaction session) {
		return this.queryForObject("getUserDetails", user, User.class, session);
	}

	/**
	 * Get all users
	 */
	public List<User> getAllUsers(final UserParam user, final Transaction session) {
		return this.queryForList("getAllUsers", user, User.class, session);
	}

	public List<User> getAllUsersWithoutApiKey(final UserParam user, final Transaction session) {
		return this.queryForList("getAllUsersWithoutApiKey", user, User.class, session);
	}

	/**
	 * Insert attributes for new user account including new Api key.
	 */
	public void insertUser(final UserParam user, final Transaction session) {
		user.setApiKey(UserUtils.generateApiKey());
		this.insert("insertUser", user, session);
	}

	/**
	 * Delete a user.
	 */ 
	public void deleteUser(final User user, final Transaction session) {
		this.delete("deleteUser", user, session);
	}

	/**
	 * Returns all users.
	 * FIXME: duplicate for getAllUsers()
	 */
	public List<User> getUsers(final String authUser, final int start, final int end, final Transaction session) {
		final UserParam param =new UserParam();
		param.setRequestedUserName(authUser);
        param.setOffset(start);
		int limit = end - start;
		param.setLimit(limit);
		return this.getAllUsers(param, session);
	}

	/**
	 * Returns all users who are members of the specified group
	 */
	public List<User> getUsers(final String authUser, final String groupName, final int start, final int end, final Transaction session) {
		final UserParam param = new UserParam();
		param.setRequestedUserName(authUser);
		param.setGroupingName(groupName);
		param.setOffset(start);
		int limit = end - start;
		param.setLimit(limit);
		// TODO implement incl. sql-statement
		return null;
	}

	/**
	 * Returns details about a specified user
	 */
	public User getUserDetails(final String authUserName, final String userName, final Transaction session) {
		final UserParam param = new UserParam();
		param.setRequestedUserName(authUserName);
		param.setUserName(userName);
		return this.getUserDetails(param, session);
	}

	/*
	 * TODO delete should also include delete of tas beside personal information
	 */
	public void deleteUser(final String userName, final Transaction session) {
		// TODO: implement
	}

	/*
	 * TODO sql-statements are not implemented 
	 */
    public void storeUser(final User user, final boolean update, final Transaction session) {
		// UPDATE
		// user would like to update his/her personal information
		if (update == true) {
			// test if user already exist
			final List<User> userTemp = getUsers(user.getName(), 1, 1, session);

			// if the user already exists, it must be exist an user account in
			// database according give name
			if (userTemp.size() == 0) {
				throw new RuntimeException("No user for given name in database");
			} else {
				// userProve is the object, which is already written in the
				// database
				final User proveUser = userTemp.get(0);
				// comparison of user object in database and current handled
				// user object each attribute two is compared
				if (proveUser.getName() != user.getName() || proveUser.getEmail() != user.getEmail() || proveUser.getHomepage() != user.getHomepage() || proveUser.getPassword() != user.getPassword() || proveUser.getRealname() != user.getRealname()) {
					/*
					 * TODO loggen löschen einfügen
					 */
				}
			}
		} else {
			// INSERT
			// new user does not exist and would like get an account

			// FIXME if "update" isn't "true" it should be false in this else
			// block, shouldn't it?!
			if (update == false) {
				this.insert("insertUser", user, session);
			}
		}
	}

	/**
	 * Authenticate a user by comparing his submitted Api key with the one
	 * stored in the database.
	 * 
	 * @param username
	 * @param apiKey
	 * @param session
	 * @return boolean true if the Api key is correct, otherwise false
	 */
	public boolean validateUserAccess(final String username, final String apiKey, final Transaction session) {
		final UserParam param = new UserParam();
		param.setUserName(username);
		final String currentApiKey = this.getApiKeyForUser(param, session);
		return currentApiKey.equals(apiKey);
	}
}