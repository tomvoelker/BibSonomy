package org.bibsonomy.database.managers;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.database.util.LogicInterfaceHelper;
import org.bibsonomy.model.User;
import org.bibsonomy.model.UserSettings;
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
	private final BasketDatabaseManager basketDb;

	private UserDatabaseManager() {
		this.basketDb = BasketDatabaseManager.getInstance();
	}

	/**
	 * @return UserDatabaseManager
	 */
	public static UserDatabaseManager getInstance() {
		return singleton;
	}

	/**
	 * Returns all users.
	 * 
	 * @param start 
	 * @param end 
	 * @param session 
	 * @return list of all users
	 */
	public List<User> getAllUsers(final int start, final int end, final DBSession session) {
		final UserParam param = LogicInterfaceHelper.buildParam(UserParam.class, null, null, null, null, null, null, start, end, null);
		return this.queryForList("getAllUsers", param, User.class, session);
	}

	/**
	 * Get details for a given user, along with settings
	 * 
	 * @param username 
	 * @param session 
	 * @return user object
	 */
	public User getUserDetails(final String username, final DBSession session) {
		final User user = this.queryForObject("getUserDetails", username, User.class, session);
		final int numPosts = this.basketDb.getNumBasketEntries(username, session);
		user.getBasket().setNumPosts(numPosts);
		return user;
	}

	// FIXME: implement me
	private UserSettings getUserSettings(final String username, final DBSession session) {
		return this.queryForObject("getUserSettings", username, UserSettings.class, session);		
	}
	
	/**
	 * Get Api key for given user.
	 * 
	 * @param username 
	 * @param session 
	 * @return apiKey
	 */
	public String getApiKeyForUser(final String username, final DBSession session) {
		return this.queryForObject("getApiKeyForUser", username, String.class, session);
	}

	/**
	 * Generate an API key for an existing user.
	 * 
	 * @param username 
	 * @param session 
	 */
	public void updateApiKeyForUser(final String username, final DBSession session) {
		final User user = new User(username);
		if (this.getUserDetails(user.getName(), session) == null) ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Can't update API key for nonexistent user");
		user.setApiKey(UserUtils.generateApiKey());
		this.update("updateApiKeyForUser", user, session);
	}

	List<String> getUserNamesByGroupId(final Integer groupId, final DBSession session) {
		return this.queryForList("getUserNamesByGroupId", groupId, String.class, session);
	}

	/**
	 * Inserts a user into the database.
	 * 
	 * @param user 
	 * @param session 
	 * @return name of created user
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
	 * 
	 * @param userName 
	 * @param session 
	 */
	public void deleteUser(final String userName, final DBSession session) {
		// TODO this should also delete tas entries
		throw new UnsupportedOperationException("Not implemented");
	}

	/**
	 * Authenticate a user by comparing his submitted Api key with the one
	 * stored in the database.
	 * 
	 * TODO: rename
	 * 
	 * @param username 
	 * @param apiKey 
	 * @param session 
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
	 * 
	 * TODO: rename
	 * 
	 * @param username 
	 * @param password 
	 * @param session 
	 * @return boolean true if the password is correct, otherwise false
	 */
	public boolean validateUserUserAccess(final String username, final String password, final DBSession session) {
		if (present(password) == false || present(username) == false) return false;
		final User found = getUserDetails(username, session);
		return ((found != null) && (found.getPassword().equals(password)));
	}
}