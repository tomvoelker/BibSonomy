package org.bibsonomy.database.managers;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.common.exceptions.UnsupportedRelationException;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.params.beans.TagIndex;
import org.bibsonomy.database.managers.chain.user.UserChain;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.database.plugin.DatabasePluginRegistry;
import org.bibsonomy.database.util.LogicInterfaceHelper;
import org.bibsonomy.database.validation.DatabaseModelValidator;
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
	
	private final DatabasePluginRegistry plugins;
	private final AdminDatabaseManager adminDBManager;
	private final BasketDatabaseManager basketDBManager;
	private final InboxDatabaseManager inboxDBManager;

	private final DatabaseModelValidator<User> validator;

	private UserDatabaseManager() {
		this.inboxDBManager = InboxDatabaseManager.getInstance();
		this.basketDBManager = BasketDatabaseManager.getInstance();
		this.plugins = DatabasePluginRegistry.getInstance();
		this.adminDBManager = AdminDatabaseManager.getInstance();
		
		this.validator = new DatabaseModelValidator<User>();
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
		final User user = this.queryForObject("getUserDetails", lowerCaseUsername, User.class, session);
		if (user == null) {
			/*
			 * user does not exist -> create an empty (=unknown) user
			 */
			return new User();
		}
		
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
		
		return user;
	}
	
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
		if (!present(this.getUserDetails(user.getName(), session).getName())) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Can't update API key for nonexistent user");
		}
		user.setApiKey(UserUtils.generateApiKey());
		this.update("updateApiKeyForUser", user, session);
	}
	
	/**
	 * Updates only the password of a current user
	 * 
	 * @param user 
	 * @param session
	 * @return the user's name
	 */
	public String updatePasswordForUser(final User user, final DBSession session) {
		if (!present(this.getUserDetails(user.getName(), session).getName())) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Can't update password for nonexistent user");
		}
		this.update("updatePasswordForUser", user, session);
		return user.getName();
	}
	
	/**
	 * Updates the UserSettings object of a user
	 * 
	 * @param user
	 * @param session
	 * @return the user's name
	 */
	public String updateUserSettingsForUser(final User user, final DBSession session) {
		if (!present(this.getUserDetails(user.getName(), session).getName())) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Can't update user settings for nonexistent user");
		}
		this.update("updateUserSettings", user, session);
		return user.getName();
	}
	
	/**
	 * Updates the users profile (gender, hobbies, …)
	 * 
	 * @param user
	 * @param session
	 * @return the user's name
	 */
	public String updateUserProfile(final User user, final DBSession session) {
		session.beginTransaction();
		try {
			if (!present(this.getUserDetails(user.getName(), session).getName())) {
				ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Can't update user profile for nonexistent user");
			}
			this.checkUser(user, session);
			this.update("updateUserProfile", user, session);
			session.commitTransaction();
			
			return user.getName();
		} finally {
			session.endTransaction();
		}
	}

	/**
	 * TODO: improve documentation
	 * TODO: only used in tests
	 * 
	 * @param groupId
	 * @param session
	 * @return TODO
	 */
	public List<String> getUserNamesByGroupId(final Integer groupId, final DBSession session) {
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
		session.beginTransaction();
		try {
			this.insertUser(user, session);
			session.commitTransaction();
			// if we don't get an exception here, we assume the user has been created successfully
			return user.getName();
		} finally {
			session.endTransaction();
		}
	}
	
	/**
	 * checks if the user is valid
	 * @param user
	 * @param session
	 */
	private void checkUser(final User user, final DBSession session) {
		this.validator.validateFieldLength(user, user.getName(), session);	
	}

	/**
	 * Insert attributes for new user account including new Api key.
	 */
	private void insertUser(final User user, final DBSession session) {
		if (!present(user)) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "User object isn't present");
		}
		user.setApiKey(UserUtils.generateApiKey());
		
		// Generates the activationCode
        user.setActivationCode(UserUtils.generateActivationCode(user));
		
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
		
		this.checkUser(user, session);
		
		if( present(user.getOpenID()) || present(user.getLdapId())) {
		    this.insert("insertUser", user, session);
		} else {
		    this.insert("insertPendingUser", user, session);
		}
		
		/*
		 * insert openID of user in separate table if present
		 */
		if (present(user.getOpenID())) {
			this.insertOpenIDUser(user, session);
		}

		/*
		 * insert ldapUserId of user in separate table if present
		 */
		if (present(user.getLdapId())) {
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
	 * Updates a user (NOT his settings).
	 * For settings update we have {@link UserDatabaseManager#updateUserSettingsForUser(User, DBSession)}
	 * 
	 * @param user the user containing all fields to be updated
	 * @param session
	 * @return the user's name iff update was successful
	 */
	public String updateUser(final User user, final DBSession session) {
		session.beginTransaction();
		try {
			final User existingUser = this.getUserDetails(user.getName(), session);
			if (!present(existingUser.getName())) { 
				ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "User '" + user.getName() + "' does not exist");
			}
			
			// update user (does not incl. userSettings)
			UserUtils.updateUser(existingUser, user);
			/*
			 * FIXME: OpenID and LdapId were updated in existingUser
			 * but the current "updateUser" Statement will leave those fields unchanged in the database
			 */
			this.plugins.onUserUpdate(existingUser.getName(), session);
			
			this.checkUser(existingUser, session);
			this.update("updateUser", existingUser, session);
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
		
		return user.getName();
	}


    private void deletePendingUser(final String username, final DBSession session) {
        if (username == null) {
            ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "username was null");
        }
        this.delete("deletePendingUser", username, session);
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
		 * FIXME: we must remove the user's open ID from the corresponding table, 
		 * otherwise a new registration with that ID is not possible.
		 */
		
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
	 * @param username 
	 * @param apiKey 
	 * @param session 
	 * @return A User object containing the user if the login succeeded. If not, 
	 * the object contains a <code>null</code> user name. 
	 */
	public User validateUserAccessByAPIKey(final String username, final String apiKey, final DBSession session) {
		// empty user object for not-logged in users
		final User notLoggedInUser = new User();

		// either username or password not given -> user is not logged in
		if (!present(apiKey) || !present(username)) {
			return notLoggedInUser;
		}

		// get user from database
		final User foundUser = this.getUserDetails(username, session);

		// user exists and api key is correct and user is no spammer
		if (foundUser.getName() != null && !foundUser.isSpammer() && foundUser.getApiKey() != null && foundUser.getApiKey().equals(apiKey)) {
			return foundUser;
		}

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
	 * @param username 
	 * @param password 
	 * @param session 
	 * @return A User object containing the user if the login succeeded. If not, 
	 * the object contains a <code>null</code> user name.
	 */
	public User validateUserAccessByPassword(final String username, final String password, final DBSession session) {
		// empty user object for not-logged in users
		final User notLoggedInUser = new User();

		// either username or password not given -> user is not logged in
		if (!present(password) || !present(username)) {
			return notLoggedInUser;
		}

		// get user from database
		final User foundUser = this.getUserDetails(username, session);

		// user exists and password is correct
		if ((foundUser.getName() != null) && (foundUser.getPassword().equals(password))) {
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
		
	/**
	 * TODO: improve documentation
	 * 
	 * @param user
	 * @param session
	 * @return TODO: documentation
	 */
	public String updateLastLdapRequest(final User user, final DBSession session) {
		this.update("updateLastLdapRequestDateForLdapUserIdByUsername", user.getName(), session);
		return user.getName();
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
		final UserParam param = new UserParam();
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
	public String getOpenIDUser(final String openID, final DBSession session) {
		return this.queryForObject("getOpenIDUser", openID, String.class, session);
	}
	
	/**
	 * Gets a ldapUserId by username 
	 * @param username
	 * @param session
	 * @return ldapUserId
	 */
	public String getLdapUserByUsername(final String username, final DBSession session) {
		return this.queryForObject("getLdapUserIdByUsername", username, String.class, session);
	}
	
	/**
	 * Gets a username by ldapUserId
	 * 
	 * @param ldapUser
	 * @param session
	 * @return username
	 */
	public String getUsernameByLdapUser(final String ldapUser, final DBSession session) {
		return this.queryForObject("getUsernameByLdapUser", ldapUser, String.class, session);
	}
	
	/**
	 * Create a relation between two users
	 * 
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
		this.insert("insertRelation_" + relation.toString(), param, session);
	}
	
	/**
	 * Get every User, that is in a specified relation with a given user:
	 * 
	 * @param sourceUser - user on the left side of the relation
	 * @param relation - currently available: FOLLOWER_OF, OF_FOLLOWER, OF_FRIEND, FRIEND_OF
	 * @param session 
	 * @return as List all users, that are in relation with the sourceUser i.e. All users u such that (sourceUser, u)\in relation
	 */
	public List<User> getUserRelation(final String sourceUser, final UserRelation relation, final DBSession session) {
		switch (relation) {
		case FOLLOWER_OF:
			/*
			 * get all Users, that the sourceUser follows
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
		return this.queryForList("getRelation_" + relation.toString(), sourceUser, User.class, session);
	}
	
	/**
	 * Delete a relation between two users (if present)
	 * 
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
	public List<User> getRelatedUsersByFolkrankAndUser(final String requestedUsername, final String loginUserName, final int limit, final int offset, final DBSession session) {
		final UserParam param = new UserParam();
		param.setRequestedUserName(requestedUsername);
		param.setUserName(loginUserName);
		param.setOffset(offset);
		param.setLimit(limit);
		return this.queryForList("getRelatedUsersByFolkrankAndUser", param, User.class, session);
	}
	
	/**
	 * Returns a a list of related users to a given users, based on a similarity computation
	 * between users.
	 * 
	 * @param requestedUserName - the requested user
	 * @param relation - the type of user relation
	 * @param loginUserName - the name of the logged-in user
	 * @param limit FIXME: unused
	 * @param offset FIXME: unused
	 * @param session
	 * @return a list of users, related to the requestedUser by the given relation
	 */
	public List<User> getRelatedUsersBySimilarity(final String requestedUserName, final String loginUserName, final UserRelation relation, final int limit, final int offset, final DBSession session) {
		final UserParam param = new UserParam();
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
	public List<User> getUsers(final UserParam param, final DBSession session) {
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
	public boolean isFollowerOfUser(final User possibleFollower, final User targetUser, final DBSession session) {
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
    
    /**
     * Activates the user. This moves the user entry from the pendingUser to the user table.
     *
     * @param user 
     * @param session 
     * @return name of created user
     */
    public String activateUser(final User user, final DBSession session) {
        this.insert("activateUser", user.getName(), session);
        this.deletePendingUser(user.getName(), session);
        return user.getName();
    }

    /**
     * Returns pending users.
     * 
     * @param start 
     * @param end 
     * @param session 
     * @return list of all users
     */
    public List<User> getPendingUsers(final int start, final int end,  final DBSession session) {
        final UserParam param = LogicInterfaceHelper.buildParam(UserParam.class, null, null, null, null, null, start, end, null, null, new User());     
        return this.queryForList("getPendingUsers", param, User.class, session);
    }

    /**
     * returns all pending users by activation code
     * 
     * @param search
     * @param start
     * @param end
     * @param session
     * @return a list of users with the specified activation code (search)
     */
    public List<User> getPendingUserByActivationCode(final String search, final int start, final int end,  final DBSession session) {	
    	// FIXME: what to do if search is null
    	final UserParam param = LogicInterfaceHelper.buildParam(UserParam.class, null, null, null, null, null, start, end, search, null, new User());     
        return this.queryForList("getPendingUserByActivationCode", param, User.class, session);
    }

    /**
     * returns all pending users by username
     * 
     * @param username
     * @param start
     * @param end
     * @param session
     * @return  a list of users with the username
     */
    public List<User> getPendingUserByUsername(final String username, final int start, final int end,  final DBSession session) {
        final UserParam param = LogicInterfaceHelper.buildParam(UserParam.class, null, username, null, null, null, start, end, null, null, new User());     
        return this.queryForList("getPendingUserByUsername", param, User.class, session);
    }	
}