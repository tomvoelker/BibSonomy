package org.bibsonomy.database.managers;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.managers.chain.user.UserChain;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.database.params.beans.TagIndex;
import org.bibsonomy.database.plugin.DatabasePluginRegistry;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.database.util.LogicInterfaceHelper;
import org.bibsonomy.model.Group;
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

	private static final Log log = LogFactory.getLog(UserDatabaseManager.class);
	private final static UserDatabaseManager singleton = new UserDatabaseManager();
	private final BasketDatabaseManager basketDb;
	private final DatabasePluginRegistry plugins;
	private static final UserChain chain = new UserChain();

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
		final UserParam param = LogicInterfaceHelper.buildParam(UserParam.class, null, null, null, null, null, null, start, end, null, null, null);		
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
		final String lowerCaseUsername = username.toLowerCase();
		User user = this.queryForObject("getUserDetails", lowerCaseUsername, User.class, session);
		if (user == null) {
			/*
			 * user does not exist -> create an empty (=unknown) user
			 */
			user = new User();
		} else {
			/*
			 * user exists: get number of posts in his basket
			 */
			final int numPosts = this.basketDb.getNumBasketEntries(lowerCaseUsername, session);
			user.getBasket().setNumPosts(numPosts);
			/*
			 * get the settings of the user
			 */
			user.setSettings(this.getUserSettings(lowerCaseUsername, session));
		}
		return user;
	}

	// FIXME: implement me
	private UserSettings getUserSettings(final String username, final DBSession session) {
		final UserParam param = new UserParam();
		param.setUserName(username);
		return this.queryForObject("getUserSettings", param, UserSettings.class, session);		
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
		if (present(user) == false) ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "User object isn't present");
		user.setApiKey(UserUtils.generateApiKey());
		
		/*
		 * The spammer column in MySQL is defined as
		 * 
		 * `spammer` tinyint(1) NOT NULL default '0'
		 * 
		 * This means, it can't take NULL values. NULL in the user object
		 * means, we don't know the spammer status, or won't change it.
		 * On insert we have to map this to 0 or 1 for the database to not
		 * throw an exception. This is done here:
		 * null, false map to 0  
		 * true maps to 1
		 * 
		 */
		user.setSpammer(user.isSpammer());
		/*
		 * The reason for the next statement is similar to user.setSpammer().
		 * 
		 * The default value is 1.
		 * 
		 * See also <48BC063F.5030307@cs.uni-kassel.de>.
		 * 
		 */
		user.setToClassify(user.getToClassify() == null ? 1 : user.getToClassify());
		/*
		 * set user's default role
		 */
		user.setRole(Role.DEFAULT);
		/*
		 * probably, we should add here more code to check for null values!
		 */
		
		
		this.insert("insertUser", user, session);
		
		/*
		 * insert openID of user in separate table if present
		 */
		if (present(user.getOpenID())) {
			this.insertOpenIDUser(user, session);
		}
	}
	
	/**
	 * Inserts a user to openID table
	 * 
	 * @param user user authenticating via OpenID
	 * @param session
	 */
	private void insertOpenIDUser(final User user, final DBSession session) {
		this.insert("insertOpenIDUser", user, session);
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

		if (present(existingUser.getName()) == false)
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "User '" + user.getName() + "' does not exist");

			
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
		existingUser.setOpenID(!present(user.getOpenID())       ? existingUser.getOpenID()      : user.getOpenID());

		// we don't want to change the registration date on update!
		//existingUser.setRegistrationDate(!present(user.getRegistrationDate()) ? existingUser.getRegistrationDate() : user.getRegistrationDate());

		existingUser.setUpdatedBy(!present(user.getUpdatedBy()) 	? existingUser.getUpdatedBy() 	: user.getUpdatedBy());
		existingUser.setUpdatedAt(!present(user.getUpdatedAt()) 	? existingUser.getUpdatedAt() 	: user.getUpdatedAt());

		existingUser.setReminderPassword(!present(user.getReminderPassword()) ? existingUser.getReminderPassword() : user.getReminderPassword());
		existingUser.setReminderPasswordRequestDate(!present(user.getReminderPasswordRequestDate()) ? existingUser.getReminderPasswordRequestDate() : user.getReminderPasswordRequestDate());

		/*
		 * FIXME: user settings are completely missing!
		 */

		this.plugins.onUserUpdate(existingUser.getName(), session);

		// TODO: update existing dataset instead of delete and re-insert
		this.removeUser(existingUser.getName(), session);
		this.insert("insertUser", existingUser, session);	
	}
	
	/**
	 * Remove a user from the database. This function executes a DELETE on the user 
	 * and openid-user table and erases all data about this user.
	 * 
	 * ATTENTION: This function is used ONLY by the updateUser-method, which works
	 * currently in the old-fashioned "delete-insert" style. As soon as we implement
	 * a proper update method, this removeUser-Method can be deleted.
	 * 
	 * @param userName 
	 * 			- a user name 
	 * @param session 
	 * 			- DB session
	 */
	private void removeUser(final String userName, final DBSession session) {
		if (present(userName) == false) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Username not set");
		}
		// TODO this should also delete tas entries
		this.delete("deleteUser", userName, session);
		
		// 2009/03/10, fei: removing openids during user update drops them,
		//        as 'getUserDetails' doesn't fetch user's openid(s)
		//        @see org.bibsonomy.database.DBLogic#storeUser(final User user, final boolean update)
		//this.delete("deleteOpenIDUser", userName, session);		
		
		//throw new UnsupportedOperationException("Not implemented");
	}

	/**
	 * Delete a user. This is the method to be called from the LogicInterface; it does not
	 * truly REMOVE a user from the DB, but does
	 * 
	 * <ul>
	 * 	 <li>flag this user as spammer</li>
	 *   <li>flags all his posts as spam</li>
	 *   <li>removes him from all groups he is a member of</li>
	 *   <li>sets his password to inactive</li>
	 * </ul>
	 * 
	 * @param user 
	 * 			- a user to be deleteed
	 * @param session 
	 * 			- DB session
	 * @throws UnsupportedOperationException
	 * 			- when this user is a group, he cannot be deleted
	 */
	public void deleteUser(final User user, final DBSession session) {
		if (user == null || present(user.getName()) == false) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Username not set");
		}
		
		GroupDatabaseManager _groupDBManager = GroupDatabaseManager.getInstance();
		AdminDatabaseManager _adminDBManager = AdminDatabaseManager.getInstance();
		
		Group _testGroup = _groupDBManager.getGroupByName(user.getName(), session);
		
		// if user is a group stop deleting and throw exception
		if (_testGroup != null){
			throw new UnsupportedOperationException("User " + user.getName() +  " is a group and can't be deleted");
		}
		
		// reset user password, set spammer flag
		final User localUser = this.getUserDetails(user.getName(), session);
		
		localUser.setPassword("inactive"); // FIXME: this must be documented and refactored into a constant!
		localUser.setRole(Role.DELETED);   // this is new - use it to check if a user has been deleted!
		
		// FIXME: Why is this necessary here, and is not performed by the flagSpammer method below?
		if (!localUser.isSpammer()){
			localUser.setSpammer(true);
		}		
		this.updateUser(localUser, session);
				
		// before deleting user remove it from all non-special groups
		List<Group> groups = _groupDBManager.getGroupsForUser(localUser.getName(), true, session);
		 
		for(Group g : groups){
			_groupDBManager.removeUserFromGroup(g.getName(), localUser.getName(), session);
		}
		
		// flag user as spammer & all his posts as spam
		localUser.setAlgorithm("self_deleted");
		_adminDBManager.flagSpammer(localUser, "on_delete", session);				
	}

	/**
	 * Authenticate a user by comparing his submitted Api key with the one
	 * stored in the database. Spammers are excluded from accessing the API
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

		// user exists and api key is correct and user is no spammer
		if (foundUser.getName() != null && !foundUser.isSpammer() && foundUser.getApiKey() != null && foundUser.getApiKey().equals(apiKey)) return foundUser;

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
	 * Retrieve a list of related users by folkrank for a given list of tags
	 * 
	 * @param tagIndex - the list of tags (as tag index)
	 * @param limit
	 * @param offset
	 * @param session
	 * @return a list of users, related by folkrank for a given list of tags
	 */
	public List<User> getRelatedUsersByFolkrankAndTags(final  List<TagIndex> tagIndex, final int limit, final int offset, final DBSession session) {
		UserParam param = new UserParam();
		param.setTagIndex(tagIndex);
		param.setLimit(limit);
		param.setOffset(offset);
		return this.queryForList("getRelatedUsersByFolkrankAndTags", param, User.class, session);
	}

	/**
	 * Gets a username by openid
	 * @param openID
	 * @param session
	 * @return username
	 */
	public String getOpenIDUser(String openID, DBSession session) {
		return this.queryForObject("getOpenIDUser", openID, String.class, session);
	}
	
	/**
	 * Returns all users which have authUser in their friend list.
	 * 
	 * @param authUser 
	 * @param session
	 * @return list of users
	 */
	public List<User> getUserFriends(final String authUser, final DBSession session) {
		return this.queryForList("getUserFriends", authUser, User.class, session);
	}

	/**
	 * Returns a list of friends for the given user. This list contains all users, which
	 * <code>authUser</code> has in his/her friend list.
	 * 
	 * @param authUser
	 * @param session
	 * @return a list of users
	 */
	public List<User> getFriendsOfUser(final String authUser, final DBSession session) {
		return this.queryForList("getFriendsOfUser", authUser, User.class, session);
	}
	
	/**
	 * 
	 * @param authUser
	 * @param session
	 * @return a list of user which the given user is following
	 */
	public List<User> getUserFollowers(final String authUser, final DBSession session){
		return this.queryForList("getUserFollowers", authUser, User.class, session);
	}
	
	/**
	 * 
	 * @param authUser
	 * @param session
	 * @return a list of user which are following the given user
	 */
	public List<User> getFollowersOfUser(final String authUser, final DBSession session){
		return this.queryForList("getFollowersOfUser", authUser, User.class, session);
	}

	/**
	 * Returns a list of users which are related to a given user by folkrank.
	 * 
	 * @param requestedUsername - the given user  
	 * @param limit
	 * @param offset
	 * @param session - the DB session
	 * @return a list of users, related by folkrank to the given user. 
	 */
	public List<User> getRelatedUsersByFolkrankAndUser(final String requestedUsername, int limit, int offset, final DBSession session) {
		UserParam param = new UserParam();
		param.setRequestedUserName(requestedUsername);
		param.setOffset(offset);
		param.setLimit(limit);
		return this.queryForList("getRelatedUsersByFolkrankAndUser", param, User.class, session);
	}
	
	
	/**
	 * Returns a a list of related users to a given users, bassed on a similarity computation
	 * between users.
	 * 
	 * @param requestedUserName - the requested user
	 * @param relation - the type of user relation
	 * @param limit
	 * @param offset
	 * @param session
	 * @return a list of users, related to the requestedUser by the given relation
	 */
	public List<User> getRelatedUsersBySimilarity(final String requestedUserName, final UserRelation relation, final int limit, final int offset, final DBSession session) {
		UserParam param = new UserParam();
		param.setRequestedUserName(requestedUserName);
		param.setUserRelation(relation);
		return this.queryForList("getRelatedUsersBySimilarity", param, User.class, session);
	}
	
	/**
	 * Entry method for the user chain
	 * 
	 * @param param
	 * @param session
	 * @return a list of user by given parameter
	 */
	public List<User> getUsers(UserParam param, final DBSession session) {
		return chain.getFirstElement().perform(param, session);
	}
	
	/**
	 * This method quits a friendship between logged in user and
	 * requested user.
	 * 
	 * @param param
	 * @param session
	 */
	public void deleteFriendOfUser(UserParam param, final DBSession session){
		this.plugins.onDeleteFriendship(param, session);
		this.delete("deleteFriendOfUser", param, session);
	}
	
	/**
	 * This method quits a fellowship between logged in user and
	 * requested user.
	 * 
	 * @param param
	 * @param session
	 */
	public void deleteFollowerOfUser(UserParam param, final DBSession session){
		this.plugins.onDeleteFellowship(param, session);
		this.delete("deleteFollowerOfUser", param, session);
	}
	
	/**
	 * This method adds a user as a follower of another user to the db.
	 * 
	 * @param param
	 * @param session
	 */
	public void addFollowerOfUser(final UserParam param, final DBSession session){
		this.insert("insertFollowerOfUser", param, session);
	}

	
	/**
	 * Check if one user follows another one.
	 * 
	 * @param possibleFollower - a possible follower
	 * @param targetUser - a user which is to be followed
	 * @param session - DB session
	 * @return true if sourceUser follows the possible
	 */
	public Boolean isFollowerOfUser(User possibleFollower, User targetUser, final DBSession session) {
		if (possibleFollower == null || targetUser == null) {
			return false;
		}
		List<User> followingUsers = this.getFollowersOfUser(possibleFollower.getName(), session);		
		for (User u : followingUsers) {
			if ( u.getName().equals(targetUser.getName()) ) {
				return true;
			}
		}
		return false;
	}	
}