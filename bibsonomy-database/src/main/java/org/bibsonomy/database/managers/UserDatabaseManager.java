package org.bibsonomy.database.managers;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.common.exceptions.AuthRequiredException;
import org.bibsonomy.common.exceptions.UnsupportedRelationException;
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
 * @author Sven Stefani
 * @version $Id$
 */
public class UserDatabaseManager extends AbstractDatabaseManager {
	
	private static final Log log = LogFactory.getLog(UserDatabaseManager.class);
	
	private static final UserDatabaseManager singleton = new UserDatabaseManager();
	
	private static final UserChain chain = new UserChain();
	
	/**
	 * @return UserDatabaseManager
	 */
	public static UserDatabaseManager getInstance() {
		return singleton;
	}
	
	private final BasketDatabaseManager basketDBManager;
	private final InboxDatabaseManager inboxDBManager;
	private final DatabasePluginRegistry plugins;
	private final AdminDatabaseManager adminDBManager;

	private UserDatabaseManager() {
		this.inboxDBManager = InboxDatabaseManager.getInstance();
		this.basketDBManager = BasketDatabaseManager.getInstance();
		this.plugins = DatabasePluginRegistry.getInstance();
		this.adminDBManager = AdminDatabaseManager.getInstance();
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
		final UserParam param = LogicInterfaceHelper.buildParam(UserParam.class, null, null, null, null, null, start, end, null, null, new User());		
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
			 * user exists: get number of posts in his basket and inbox
			 */
			final int numPosts = this.basketDBManager.getNumBasketEntries(lowerCaseUsername, session);
			user.getBasket().setNumPosts(numPosts);
			final int inboxMessages = this.inboxDBManager.getNumInboxMessages(lowerCaseUsername, session);
			user.getInbox().setNumPosts(inboxMessages);
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
	
	/**
	 * Updates only the password of a current user
	 * @param user 
	 * @param session
	 * @return
	 */
	public String updatePasswordForUser(final User user, final DBSession session) {
		if (this.getUserDetails(user.getName(), session).getName() == null) ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Can't update password for nonexistent user");
		this.update("updatePasswordForUser", user, session);
		return user.getName();
	}
	
	/**
	 * Updates the UserSettings object of a user
	 * @param user
	 * @param session
	 * @return
	 */
	public String updateUserSettingsForUser(User user, final DBSession session) {
		if (this.getUserDetails(user.getName(), session).getName() == null) ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Can't update user settings for nonexistent user");
		this.update("updateUserSettings", user, session);
		return user.getName();
	}
	
	public String updateUserProfile(User user, final DBSession session) {
		if (this.getUserDetails(user.getName(), session).getName() == null) ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Can't update user profile for nonexistent user");
		this.update("updateUserProfile", user, session);
		return user.getName();
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

		/*
		 * insert ldapUserId of user in separate table if present
		 */
		if (present(user.getUserId())) {
			this.insertLdapUserId(user, session);
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
	 * Inserts a user to ldapUser table
	 * 
	 * @param user user authenticating via ldap
	 * @param session
	 */
	private void insertLdapUserId(final User user, final DBSession session) {
		this.insert("insertLdapUser", user, session);
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
		existingUser.setUserId(!present(user.getUserId())       ? existingUser.getUserId()      : user.getUserId());

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
	 * @param userName
	 * 			- the name of the user to be deleteed
	 * @param session 
	 * 			- DB session
	 * @throws UnsupportedOperationException
	 * 			- when this user is a group, he cannot be deleted
	 */
	public void deleteUser(final String userName, final DBSession session) {
		if (!present(userName)) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "user name not set");
		}
		/*
		 * We can't use a global (i.e., class attribute) manager, since the 
		 * GroupDatabaseManager contains a UserDatabaseManager and thus we 
		 * have a circular dependency in the constructors.
		 */
		final GroupDatabaseManager groupDBManager = GroupDatabaseManager.getInstance();
		
		/*
		 * if user is a group stop deleting and throw exception
		 */
		if (present(groupDBManager.getGroupByName(userName, session))){
			throw new UnsupportedOperationException("User " + userName +  " is a group and thus can't be deleted. Please contact the webmaster.");
		}
		
		/*
		 * a deleted user the folllowing properties:
		 * - his password "hash" is "inactive" (literally!)
		 * - his role is "DELETED"
		 * - his spam status is "true"
		 * - his algorithm is "self_deleted"
		 * - the groups of all his posts are set to spam groups
		 */
		final User user = this.getUserDetails(userName, session);
		user.setPassword("inactive"); // FIXME: this must be documented and refactored into a constant!
		user.setRole(Role.DELETED);   // this is new - use it to check if a user has been deleted!
		user.setSpammer(true);        // FIXME: Why is this necessary here, and is not performed by the flagSpammer method below?
		this.updateUser(user, session);

		
		/*
		 * before deleting user remove it from all non-special groups
		 */
		final List<Group> groups = groupDBManager.getGroupsForUser(userName, true, session);
		for (final Group group: groups){
			groupDBManager.removeUserFromGroup(group.getName(), userName, session);
		}
		
		/*
		 * flag user as spammer & all his posts as spam
		 */
		user.setAlgorithm("self_deleted");
		adminDBManager.flagSpammer(user, "on_delete", session);				
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
	 * @throws AuthRequiredException 
	 */
	public User validateUserUserAccess(final String username, final String password, final DBSession session) throws AuthRequiredException {
		// empty user object for not-logged in users
		final User notLoggedInUser = new User();

		// either username or password not given -> user is not logged in
		if (present(password) == false || present(username) == false) return notLoggedInUser;

		// get user from database
		final User foundUser = getUserDetails(username, session);

		// user exists and password is correct
		if ((foundUser.getName() != null) && (foundUser.getPassword().equals(password))) {
	
			/* 
			 * check, if it is an ldap user and if it has to re-auth agains ldap server. if so, do it.
			 */
			// if user database authentication was successful
			// check if user is listed in ldapUser table
			if (this.isLdapUser(username, session))
			{
			
				// get date of last authentication against ldap server

				Date userLastAccess = this.userLastLdapRequest(username, session);
				
				// TODO: get timeToReAuth from tomcat's environment, so a user can adjust is without editing code  
				int timeToReAuth =  18  *60*60; // seconds
				Date dateNow = new Date();
				// timeDiff is in seconds
				long timeDiff = (dateNow.getTime() - userLastAccess.getTime())/1000;						
				
				log.info("last access of user "+username+" was on "+userLastAccess.toString()+ " ("+(timeDiff/3600)+" hours ago = "+ " ("+(timeDiff/60)+" minutes ago = "+timeDiff+" seconds ago)");
//DEBUG
//timeDiff=timeToReAuth;
			
				/*
				 *  check lastAccess - re-auth required?
				 *  if time of last access is too far away, re-authenticate against ldap server to check
				 *  whether password is same or user exists anymore
				 */
				
				if ( timeDiff > timeToReAuth ) {
					// re-auth
					log.info("last access time is up - ldap re-auth required -> throw reauthrequiredException");
					
					throw new AuthRequiredException("last access time is up - ldap re-auth required");
					
				}
			}		
			return foundUser;
		}
		
		// fallback: user is not logged in
		return notLoggedInUser;
	}

	/**
	 * check if user is in table ldapUser.
	 * 
	 * @param userName
	 * @param session
	 * @return true if user is in table LdapUser, otherwise false 
	 */
	public boolean isLdapUser(final String userName, final DBSession session) {
		return present(this.queryForObject("getLdapUserIdByUsername", userName, String.class, session));
	}
	
	/**
	 * check if user is in table ldapUser.
	 * 
	 * @param userName
	 * @param session
	 * @return true if user is in table LdapUser, otherwise false 
	 */
	public Date userLastLdapRequest(final String userName, final DBSession session) {
		return this.queryForObject("getLastLdapRequestByUsername", userName, Date.class, session);
	}
		
	public void updateLastLdapRequest(final String userName, final DBSession session) {
		this.update("updateLastLdapRequestDateForLdapUserIdByUsername", userName, session);
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
	 * Gets a ldapUserId by username 
	 * @param username
	 * @param session
	 * @return ldapUserId
	 */
	public String getLdapUserByUsername(String username, DBSession session) {
		return this.queryForObject("getLdapUserIdByUsername", username, String.class, session);
	}
	
	/**
	 * Gets a username by ldapUserId
	 * @param ldapUser
	 * @param session
	 * @return username
	 */
	public String getUsernameByLdapUser(String ldapUser, DBSession session) {
		return this.queryForObject("getUsernameByLdapUser", ldapUser, String.class, session);
	}
	
	
	//*****************
	// USER RELATIONS *
	//*****************
	
	/**
	 * Create a relation between two users
	 * @param sourceUser (left side of the relation)
	 * @param targetUser (right side of the relation)
	 * @param relation currently available: FOLLOWER_OF, OF_FRIEND 
	 * (their duals not included intentionally, since these relations 
	 * should only be modified by the targetUser (right hand side of the relation) 
	 * @param session 
	 * result: (sourceUser, targetUser) \in relation
	 */
	public void createUserRelation(final String sourceUser, final String targetUser, final UserRelation relation, final DBSession session) {
		switch (relation) {
			case FOLLOWER_OF:
				/*
				 *  sourceUser will now follow targetUser therefore
				 *  (sourceUser, targetUser)\in FOLLOWER_OF
				 *  
				 */
				break;
			case OF_FRIEND:
				/* 
				 * this means: sourceUser adds a new friend (targetUser) and therefore 
				 * (sourceUser, targetUser)\in OF_FRIEND
				 * = targetUser is a friend of sourceUser = targetUser is in sourceUser's friendsList
				 */
				break;
			default:
				/* 
				 * Since we use relations and there duals it is sometimes difficult to be sure which of our relation fits properly
				 * Therefore it is not advised to add the dual relations.
				 * In this method you can only choose those relations, where the sourceUser (left side of the relation) 
				 * is allowed to create the relation.
				 */
				throw new UnsupportedRelationException();
		}		
		final UserParam param = new UserParam();
		param.setUserName(sourceUser);
		param.setRequestedUserName(targetUser);
		this.insert("insertRelation_"+relation.toString(), param, session);
	}
	
	/**
	 * Get every User, that is in a specified relation with a given user:
	 * @param sourceUser - user on the left side of the relation
	 * @param relation - currently available: FOLLOWER_OF, OF_FOLLOWER, OF_FRIEND, FRIEND_OF
	 * @param session 
	 * @return as List all users, that are in relation with the sourceUser i.e. All users u such that (sourceUser, u)\in relation
	 */
	public List<User> getUserRelation(final String sourceUser, final UserRelation relation, final DBSession session) {
		switch (relation) {
		case FOLLOWER_OF:
			/*
			 * get all Users, that the sourcerUser follows
			 */
			break;
		case OF_FOLLOWER:
			/*
			 * get all users, that follow the sourceUser
			 */
			break;
		case OF_FRIEND:
			/*
			 *  get all users, that sourceUser has in his friends list			 
			 */
			break;
		case FRIEND_OF:
			/*
			 * get all users, that have sourceUser in their friends list
			 */
			break;
		default:
			/*
			 * Other relations are not supported at this time 
			 */
			throw new UnsupportedRelationException();
		}		
		//list of all targetUsers for sourceUser in (the) relation
		return this.queryForList("getRelation_"+relation.toString(), sourceUser, User.class, session);
	}
	
	/**
	 * Delete a relation between two users (if present)
	 * @param sourceUser (left side of the relation)
	 * @param targetUser (right side of the relation)
	 * @param relation currently available: FOLLOWER_OF, OF_FRIEND 
	 * (their duals not included intentionally, since these relations 
	 * should only be modified by the targetUser (right hand side of the relation) 
	 * @param session 
	 * result: (sourceUser, targetUser) \notin relation
	 */
	public void deleteUserRelation(final String sourceUser, final String targetUser, final UserRelation relation, final DBSession session) {
		final UserParam param = new UserParam();
		param.setUserName(sourceUser);
		param.setRequestedUserName(targetUser);
		switch (relation) {
			case FOLLOWER_OF:
				/*
				 *  sourceUser will no longer follow targetUser therefore
				 *	delete (sourceUser, targetUser) from FOLLOWER_OF
			 	 *  
			 	 */
				this.plugins.onDeleteFellowship(param, session);
				break;
			case OF_FRIEND:
				/* 
				 * this means: sourceUser will no longer have targetUser as friend and therefore 
				 * delete (sourceUser, targetUser) from  OF_FRIEND
				 * = targetUser is no longer a friend of sourceUser = targetUser is no longer in sourceUser's friendsList
				 */
				this.plugins.onDeleteFriendship(param, session);
				break;
			default:
				/* 
				 * Since we use relations and there duals it is sometimes difficult to be sure which of our relation fits properly
				 * Therefore it is not advised to add the dual relations.
				 * In this method you can only choose those relations, where the sourceUser (left side of the relation) 
				 * is allowed to create the relation.
				 */
				throw new UnsupportedRelationException();
		}		
		this.delete("deleteRelation_"+relation.toString(), param, session);
	}
	

	/**
	 * Returns a list of users which are related to a given user by folkrank.
	 * 
	 * @param requestedUsername - the given user
	 * @param loginUserName - the logged-in user  
	 * @param limit
	 * @param offset
	 * @param session - the DB session
	 * @return a list of users, related by folkrank to the given user. 
	 */
	public List<User> getRelatedUsersByFolkrankAndUser(final String requestedUsername, final String loginUserName, int limit, int offset, final DBSession session) {
		UserParam param = new UserParam();
		param.setRequestedUserName(requestedUsername);
		param.setUserName(loginUserName);
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
	 * @param loginUserName - the name of the logged-in user
	 * @param limit
	 * @param offset
	 * @param session
	 * @return a list of users, related to the requestedUser by the given relation
	 */
	public List<User> getRelatedUsersBySimilarity(final String requestedUserName, final String loginUserName, final UserRelation relation, final int limit, final int offset, final DBSession session) {
		UserParam param = new UserParam();
		param.setRequestedUserName(requestedUserName);
		param.setUserName(loginUserName);
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
	 * Check if one user follows another one.
	 * 
	 * @param possibleFollower - a possible follower
	 * @param targetUser - a user which is to be followed
	 * @param session - DB session
	 * @return true if sourceUser follows the possible
	 */
	public Boolean isFollowerOfUser(User possibleFollower, User targetUser, final DBSession session) {
		if (!present(possibleFollower) || !present(targetUser)) {
			return false;
		}
		final List<User> followingUsers = this.getUserRelation(possibleFollower.getName(), UserRelation.FOLLOWER_OF, session);		
		for (final User u : followingUsers) {
			if ( u.getName().equalsIgnoreCase(targetUser.getName()) ) {
				return true;
			}
		}
		return false;
	}	
	
	
}