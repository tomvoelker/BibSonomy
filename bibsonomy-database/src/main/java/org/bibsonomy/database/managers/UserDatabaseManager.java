package org.bibsonomy.database.managers;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.database.util.LogicInterfaceHelper;
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

	private static final Logger log = Logger.getLogger(UserDatabaseManager.class);
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
		final UserParam param = LogicInterfaceHelper.buildParam(UserParam.class, null, null, null, null, null, null, start, end, null);
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
	 * Inserts a user into the database.
	 */
	public String createUser(final User user, final DBSession session) {
		this.insertUser(user, session);
		// if we don't get an exception here, we assume the user has been created successfully
		return user.getName();
	}

	/**
	 * Insert attributes for new user account including new Api key.
	 */
	private void insertUser(final User user, final DBSession session) {
		if (user == null) ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "User object isn't present");
		user.setApiKey(UserUtils.generateApiKey());
		this.insert("insertUser", user, session);
	}

	/**
	 * Delete a user.
	 */
	public void deleteUser(@SuppressWarnings("unused") final String userName, @SuppressWarnings("unused") final DBSession session) {
		// TODO this should also delete tas entries
		throw new UnsupportedOperationException("Not implemented");
	}

	/**
	 * Authenticate a user by comparing his submitted Api key with the one
	 * stored in the database.
	 * TODO: rename
	 * 
	 * @return boolean true if the Api key is correct, otherwise false
	 */
	public boolean validateUserAccess(final String username, final String apiKey, final DBSession session) {
		if (present(apiKey) == false || present(username) == false) return false;
		final String currentApiKey = this.getApiKeyForUser(username, session);
		if (currentApiKey == null) return false;
		return apiKey.equals(currentApiKey);
	}
	
	/**
	 * Authenticate a user by comparing his submitted password with the one
	 * stored in the database.
	 * TODO: rename
	 * 
	 * @return boolean true if the password is correct, otherwise false
	 */
	public boolean validateUserUserAccess(final String username, final String password, final DBSession session) {
		if (present(password) == false || present(username) == false) return false;
		final User found = getUserDetails(username, session);
		return ((found != null) && (found.getPassword().equals(password)));
	}
}