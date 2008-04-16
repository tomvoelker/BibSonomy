package org.bibsonomy.database.managers;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.database.plugin.DatabasePluginRegistry;
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
	private final DatabasePluginRegistry plugins;

	private UserDatabaseManager() {
		this.basketDb = BasketDatabaseManager.getInstance();
		this.plugins = DatabasePluginRegistry.getInstance();
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
		final UserParam param = LogicInterfaceHelper.buildParam(UserParam.class, null, null, null, null, null, null, start, end, null, null);		
		return this.queryForList("getAllUsers", param, User.class, session);
	}

	/**
	 * Get details for a given user, along with settings. 
	 * If the user does not exist, an empty user object (not <code>null</code>!) is returned. 
	 * The user name of that object will be <code>null</code> instead.
	 * 
	 * @param username 
	 * @param session 
	 * @return user object
	 */
	public User getUserDetails(final String username, final DBSession session) {
		User user = this.queryForObject("getUserDetails", username, User.class, session);
		if (user == null) {
			/*
			 * user does not exist -> create an empty (=unknown) user
			 */
			user = new User();
		} else {
			/*
			 * user exists: get number of posts in his basket
			 */
			final int numPosts = this.basketDb.getNumBasketEntries(username, session);
			user.getBasket().setNumPosts(numPosts);
		}
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
		if (this.getUserDetails(user.getName(), session).getName() == null) ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Can't update API key for nonexistent user");
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
	 * Change the user details
	 * @param user
	 * @param session
	 * @return the username
	 */
	public String changeUser(final User user, final DBSession session) {
		this.updateUser(user, session);
		return user.getName();
	}

	/**
	 * Updates a user
	 * @param user the user
	 * @param session
	 */
	private void updateUser(final User user, final DBSession session) {
		final User existingUser = this.getUserDetails(user.getName(), session);

		if (existingUser == null)
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "User " + user.getName() + "does not exist");

		// FIXME if this should copy all properties from the one bean to the
		// other we might want to come up with a more generic version of this
		// code block - so if we add a field to the User bean we don't have to
		// remember adding it here
		existingUser.setEmail(!present(user.getEmail()) 		? existingUser.getEmail() 		: user.getEmail());
		existingUser.setPassword(!present(user.getPassword()) 	? existingUser.getPassword() 	: user.getPassword());		
		existingUser.setRealname(!present(user.getRealname()) 	? existingUser.getRealname() 	: user.getRealname());		
		existingUser.setHomepage(!present(user.getHomepage()) 	? existingUser.getHomepage() 	: user.getHomepage());		
		existingUser.setApiKey(!present(user.getApiKey()) 		? existingUser.getApiKey()	 	: user.getApiKey());		
		existingUser.setBirthday(!present(user.getBirthday()) 	? existingUser.getBirthday() 	: user.getBirthday());
		existingUser.setGender(!present(user.getGender()) 		? existingUser.getGender() 		: user.getGender());
		existingUser.setHobbies(!present(user.getHobbies()) 	? existingUser.getHobbies() 	: user.getHobbies());
		existingUser.setInterests(!present(user.getInterests()) ? existingUser.getInterests() 	: user.getInterests());
		existingUser.setSpammer(!present(user.getSpammer()) 	? existingUser.getSpammer() 	: user.getSpammer());
		existingUser.setIPAddress(!present(user.getIPAddress()) ? existingUser.getIPAddress() 	: user.getIPAddress());
		existingUser.setOpenURL(!present(user.getOpenURL()) 	? existingUser.getOpenURL() 	: user.getOpenURL());
		existingUser.setPlace(!present(user.getPlace()) 		? existingUser.getPlace() 		: user.getPlace());
		existingUser.setProfession(!present(user.getProfession()) ? existingUser.getProfession(): user.getProfession());
		existingUser.setRegistrationDate(!present(user.getRegistrationDate()) ? existingUser.getRegistrationDate() : user.getRegistrationDate());
		
		existingUser.setUpdatedBy(!present(user.getUpdatedBy()) 	? existingUser.getUpdatedBy() 	: user.getUpdatedBy());
		existingUser.setUpdatedAt(!present(user.getUpdatedAt()) 	? existingUser.getUpdatedAt() 	: user.getUpdatedAt());
		
		this.plugins.onUserUpdate(existingUser.getName(), session);

		// TODO: update existing dataset instead of delete and re-insert
		this.deleteUser(existingUser.getName(), session);
		this.insert("insertUser", existingUser, session);	
	}

	/**
	 * Delete a user.
	 * 
	 * @param userName 
	 * @param session 
	 */
	public void deleteUser(final String userName, final DBSession session) {
		// TODO this should also delete tas entries
		this.delete("deleteUser", userName, session);
		//throw new UnsupportedOperationException("Not implemented");
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
	 * @return A User object containing the user if the login succeeded. If not, 
	 * the object contains a <code>null</code> user name. 
	 */
	public User validateUserAccess(final String username, final String apiKey, final DBSession session) {
		// empty user object for not-logged in users
		final User notLoggedInUser = new User();
		
		// either username or password not given -> user is not logged in
		if (present(apiKey) == false || present(username) == false) return notLoggedInUser;
		
		// get user from database
		final User foundUser = getUserDetails(username, session);
		
		// user exists and password is correct
		if ((foundUser.getName() != null) && (foundUser.getApiKey().equals(apiKey))) return foundUser;
		
		// fallback: user is not logged in
		return notLoggedInUser;
	}

	/**
	 * Authenticate a user by comparing his submitted password with the one
	 * stored in the database. If the user exists and the password is correct,
	 * the returned object contains the users details (including his name). If
	 * the user does not exist or the password is wrong, the user name of the 
	 * returned object is NULL. 
	 * 
	 * TODO: rename
	 * 
	 * @param username 
	 * @param password 
	 * @param session 
	 * @return A User object containing the user if the login succeeded. If not, 
	 * the object contains a <code>null</code> user name. 
	 */
	public User validateUserUserAccess(final String username, final String password, final DBSession session) {
		// empty user object for not-logged in users
		final User notLoggedInUser = new User();
		
		// either username or password not given -> user is not logged in
		if (present(password) == false || present(username) == false) return notLoggedInUser;
		
		// get user from database
		final User foundUser = getUserDetails(username, session);
		
		// user exists and password is correct
		if ((foundUser.getName() != null) && (foundUser.getPassword().equals(password))) return foundUser;
		
		// fallback: user is not logged in
		return notLoggedInUser;
	}

	/**
	 * @param param
	 * @param session
	 * @return list of users
	 */
	public List<User> getUserByFolkrank(final UserParam param, final DBSession session){
		return this.queryForList("getUsersOrderedByFolkrank", param, User.class, session);
	}
}