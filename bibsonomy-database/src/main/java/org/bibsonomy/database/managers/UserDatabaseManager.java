package org.bibsonomy.database.managers;

import java.util.List;

import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.database.util.LogicInterfaceHelper;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.UserUtils;
import org.bibsonomy.util.ExceptionUtils;

/**
 * Used to retrieve users from the database.
 * 
 * @author Dominik Benz
 * @author Miranda Grahl
 * @author Christian Schenk
 * @version $Id$
 */
public class UserDatabaseManager extends AbstractDatabaseManager {

	/** Singleton */
	private final static UserDatabaseManager singleton = new UserDatabaseManager();

	private UserDatabaseManager() {
	}

	public static UserDatabaseManager getInstance() {
		return singleton;
	}

	/**
	 * Returns all users.
	 */
	public List<User> getAllUsers(final int start, final int end, final DBSession session) {
		final UserParam param = LogicInterfaceHelper.buildParam(UserParam.class, null, null, null, null, null, null, start, end);
		return this.queryForList("getAllUsers", param, User.class, session);
	}

	/**
	 * Get details for a given user
	 */
	public User getUserDetails(final String username, final DBSession session) {
		return this.queryForObject("getUserDetails", username, User.class, session);
	}

	/**
	 * Get Api key for given user.
	 */
	public String getApiKeyForUser(final String username, final DBSession session) {
		return this.queryForObject("getApiKeyForUser", username, String.class, session);
	}

	/**
	 * Generate an API key for an existing user.
	 */
	public void updateApiKeyForUser(final User user, final DBSession session) {
		if (this.getUserDetails(user.getName(), session) == null) ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Can't update Api key for nonexistent user");
		user.setApiKey(UserUtils.generateApiKey());
		this.update("updateApiKeyForUser", user, session);
	}

	List<String> getUserNamesByGroupId(final Integer groupId, final DBSession session) {
		return this.queryForList("getUserNamesByGroupId", groupId, String.class, session);
	}

	/**
	 * Insert attributes for new user account including new Api key.
	 */
	public void insertUser(final UserParam param, final DBSession session) {
		if (param.getUser() == null) ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "User object isn't present");
		param.getUser().setApiKey(UserUtils.generateApiKey());
		this.insert("insertUser", param, session);
	}

	/**
	 * Delete a user.
	 */
	public void deleteUser(final User user, final DBSession session) {
		this.delete("deleteUser", user, session);
	}

	/*
	 * TODO delete should also include delete of tas beside personal information
	 */
	public void deleteUser(final String userName, final DBSession session) {
		// TODO: implement
	}

	/*
	 * TODO sql-statements are not implemented
	 */
	public void storeUser(final User user, final boolean update, final DBSession session) {
		// UPDATE
		// user would like to update his/her personal information
		if (update == true) {
			// test if user already exist
			final User userTemp = this.getUserDetails(user.getName(), session);

			// if the user already exists, there must be an user account in the
			// database
			if (userTemp == null) {
				ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "No user for given name in database");
			} else {
				// TODO change comparison!!!
				if (userTemp.getName() != user.getName() || userTemp.getEmail() != user.getEmail() || userTemp.getHomepage() != user.getHomepage() || userTemp.getPassword() != user.getPassword() || userTemp.getRealname() != user.getRealname()) {
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
	 * @return boolean true if the Api key is correct, otherwise false
	 */
	public boolean validateUserAccess(final String username, final String apiKey, final DBSession session) {
		if (apiKey == null || "".equals(apiKey.trim())) return false;
		if (username == null || "".equals(username.trim())) return false;
		final String currentApiKey = this.getApiKeyForUser(username, session);
		if (currentApiKey == null) {
			return false;
		} else {
			return apiKey.equals(currentApiKey);
		}
	}
}