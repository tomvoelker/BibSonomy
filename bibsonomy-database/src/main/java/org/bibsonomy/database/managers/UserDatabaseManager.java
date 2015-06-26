/**
 * BibSonomy-Database - Database for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.database.managers;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.common.exceptions.UnsupportedRelationException;
import org.bibsonomy.database.common.AbstractDatabaseManager;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.params.beans.TagIndex;
import org.bibsonomy.database.managers.chain.Chain;
import org.bibsonomy.database.params.SamlUserParam;
import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.database.params.WikiParam;
import org.bibsonomy.database.plugin.DatabasePluginRegistry;
import org.bibsonomy.database.systemstags.search.NetworkRelationSystemTag;
import org.bibsonomy.database.validation.DatabaseModelValidator;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.UserSettings;
import org.bibsonomy.model.user.remote.RemoteUserId;
import org.bibsonomy.model.user.remote.SamlRemoteUserId;
import org.bibsonomy.model.util.UserUtils;
import org.bibsonomy.model.util.file.UploadedFile;
import org.bibsonomy.services.filesystem.FileLogic;
import org.bibsonomy.util.ExceptionUtils;
import org.bibsonomy.util.file.LazyUploadedFile;
import org.bibsonomy.wiki.TemplateManager;

/**
 * Used to retrieve users from the database.
 * 
 * @author Dominik Benz
 * @author Miranda Grahl
 * @author Christian Schenk
 * @author Sven Stefani
 */
public class UserDatabaseManager extends AbstractDatabaseManager {
	private static final Log log = LogFactory.getLog(UserDatabaseManager.class);
	
	private static final Tag BIBSONOMY_FRIEND_SYSTEM_TAG = new Tag(NetworkRelationSystemTag.BibSonomyFriendSystemTag);
	private static final Tag BIBSONOMY_SPAMMER_SYSTEM_TAG = new Tag(NetworkRelationSystemTag.BibSonomySpammerSystemTag);
	
	private static final UserDatabaseManager singleton = new UserDatabaseManager();

	
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

	private DatabaseModelValidator<User> validator;
	
	private Chain<List<User>, UserParam> chain;
	
	private FileLogic fileLogic;
	
	//this should be set through ${user.defaultToClassify}, if not: 1
	private Integer usersDefaultToClassify = 1;

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
		final UserParam param = new UserParam();
		param.setOffset(start);
		param.setLimit(end);
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
		if (!present(username)) {
			return createEmptyUser();
		}
		final String lowerCaseUsername = username.toLowerCase();
		final User user = this.queryForObject("getUserDetails", lowerCaseUsername, User.class, session);
		if (user == null) {
			/*
			 * user does not exist -> create an empty (=unknown) user
			 */
			return createEmptyUser();
		}
		
		/*
		 * user exists: get number of posts in his basket and inbox
		 */
		final int numPosts = this.basketDBManager.getNumberOfBasketEntries(lowerCaseUsername, session);
		user.getBasket().setNumPosts(numPosts);
		final int inboxMessages = this.inboxDBManager.getNumInboxMessages(lowerCaseUsername, session);
		user.getInbox().setNumPosts(inboxMessages);
		/*
		 * get the settings of the user
		 * TODO: user settings are stored in the same table with attributes like name
		 * Can't we fetch the settings with the getUserDetails query?
		 */
		user.setSettings(this.getUserSettings(lowerCaseUsername, session));
		
		/*
		 * get user profile picture from fileLogic
		 */
		user.setProfilePicture(new LazyUploadedFile(){

			@Override
			protected File requestFile() {
				return fileLogic.getProfilePictureForUser(user.getName());
			}
		});
		
		/*
		 * TODO: Replace this with a more Generic Version
		 * This fetches all SamlRemoteUserIds (LDAP and OpenId are already fetched through a join with the respective tables in "getUserDetails")
		 * FIXME: Use another join in getUserDetails (or enable this query only if Saml Authentification is active) 
		 */
		for (final SamlRemoteUserId samlRemoteUserId : this.getSamlRemoteUserIds(user.getName(), session)) {
			user.setRemoteUserId(samlRemoteUserId);
		}
		
		return user;
	}
	
	/**
	 * Creates a new, empty user w/ default profile picture.
	 * @return empty user instance
	 */
	public User createEmptyUser ()
	{
		final User user = new User();
		user.setProfilePicture(new LazyUploadedFile() {
			
			@Override
			protected File requestFile() {
				//get default profile picture
				return fileLogic.getProfilePictureForUser("");
			}
		});
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

	protected void update( final String query, final User user, final DBSession session ) {
		super.update(query, user, session);
		
		//TODO replace by switch
		if (query == "updateUser" || query == "updateUserProfile") {
			final UploadedFile profilePicture = user.getProfilePicture();
			
			if ( !present(profilePicture) )
				//nothing to do
				return;
			
			/*
			 * If profile picture file given for upload -> upload
			 * If profile picture has been deleted -> delete
			 */
			switch( profilePicture.getPurpose() )
			{
			case UPLOAD:
				try {
					fileLogic.saveProfilePictureForUser( user.getName(), profilePicture );
				} catch (final Exception ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
				break;
			case DELETE:
				fileLogic.deleteProfilePictureForUser( user.getName() );
				break;
			default:
				//nothing to do
			}
		}
	}
	
	/**
	 * Generate an API key for an existing user.
	 * 
	 * @param user 
	 * @param session 
	 */
	public void updateApiKeyForUser(final User user, final DBSession session) {
		if (!present(this.getUserDetails(user.getName(), session).getName())) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Can't update API key for nonexistent user");
		}
		user.setApiKey(UserUtils.generateApiKey());
		this.plugins.onUserUpdate(user.getName(), session);
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
		final String userName = user.getName();
		if (!present(this.getUserDetails(userName, session).getName())) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Can't update password for nonexistent user");
		}
		this.plugins.onUserUpdate(userName, session);
		this.update("updatePasswordForUser", user, session);
		return userName;
	}
	
	/**
	 * Updates the UserSettings object of a user
	 * 
	 * @param user
	 * @param session
	 * @return the user's name
	 */
	public String updateUserSettingsForUser(final User user, final DBSession session) {
		final String userName = user.getName();
		if (!present(this.getUserDetails(userName, session).getName())) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Can't update user settings for nonexistent user");
		}
		this.plugins.onUserUpdate(userName, session);
		this.update("updateUserSettings", user, session);
		return userName;
	}
	
	/**
	 * Updates properties that need to be updated when a limited user is set to unlimited.
	 * @param user
	 * @param session
	 * @return
	 */
	public String updateLimitedUser(final User user, final DBSession session) {
		final String userName = user.getName();
		if (!present(this.getUserDetails(userName, session).getName())) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Can't update role of a nonexistent user");
		}
		this.plugins.onUserUpdate(userName, session);
		this.update("updateLimitedUser", user, session);
		return userName;
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
			final String userName = user.getName();
			if (!present(this.getUserDetails(userName, session).getName())) {
				ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Can't update user profile for nonexistent user");
			}
			this.checkUser(user, session);
			this.plugins.onUserUpdate(userName, session);
			this.update("updateUserProfile", user, session);
			session.commitTransaction();
			
			return userName;
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
		user.setToClassify(user.getToClassify() == null ? usersDefaultToClassify : user.getToClassify());
		/*
		 * if it is not a limited or groupuser that is to be inserted, set user's default role
		 */
		if (! (Role.LIMITED.equals(user.getRole()) || Role.GROUPUSER.equals(user.getRole()))) {
			user.setRole(Role.DEFAULT);
		}
		/*
		 * probably, we should add here more code to check for null values!
		 */
		this.checkUser(user, session);
		
		/* no email validation for openid/ldap/remoteId(currently only saml) users */
		if (present(user.getOpenID()) || present(user.getLdapId()) || (present(user.getRemoteUserIds()))) {
			this.insert("insertUser", user, session);
			this.insertDefaultWiki(user, session);
		} else {
			// generates the activationCode
			user.setActivationCode(UserUtils.generateActivationCode(user));
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
		
		/*
		 * insert remote UserIds of user in separate table if present (currently only saml)
		 */
		for (final RemoteUserId ruid : user.getRemoteUserIds()) {
			this.insertRemoteUserId(user, ruid, session);
		}
	}
	
	/**
	 * Inserts a user to a remoteUser table (at present only samlRemoteUserIds are handled)
	 * @param user
	 * @param remoteUserId
	 * @param session
	 */
	private void insertRemoteUserId(final User user, final RemoteUserId remoteUserId, final DBSession session) {
		if (remoteUserId instanceof SamlRemoteUserId) {
			this.insertSamlUserId(new SamlUserParam(user, (SamlRemoteUserId)remoteUserId), session);
		} else {
			throw new IllegalArgumentException("Only SamlRemoteUserIds can be inserted!");
		}
		/*
		else if (remoteUserId instanceof OpenIdRemoteUserId) {
			this.insertOpenIdUserId(new OpenIdRemoteUserParam(user, remoteUserId), session);
		}
		else if (remoteUserId instanceof LdapRemoteUserId) {
			this.insertLdapUserId(new LdapRemoteUserParam(user, remoteUserId), session);
		}
		*/
	}

	private void insertSamlUserId(final SamlUserParam samlRemoteUserParam, final DBSession session) {
		this.insert("insertSamlUserId", samlRemoteUserParam, session);
	}
	/*
	private void insertOpenIdUserId(OpenIdRemoteUserParam remoteUserParam, DBSession session) {
		this.insert("insertOpenIdUserId", remoteUserParam, session);
	}
	private void insertLdapUserId(LdapRemoteUserParam remoteUserParam, DBSession session) {
		this.insert("insertLdapUserId", remoteUserParam, session);
	}
	*/
	
	/**
	 * Deletes a remoteUserId from remoteUser tables (at present only samlremoteUserids are handled)
	 * @param samlRemoteUserId
	 * @param session
	 */
	public void deleteRemoteUserId(final RemoteUserId samlRemoteUserId, final DBSession session) {
		if (samlRemoteUserId instanceof SamlRemoteUserId) {
			this.deleteSamlUserId(new SamlUserParam(null, (SamlRemoteUserId)samlRemoteUserId), session);
		} else {
			throw new IllegalArgumentException("Only SamlRemoteUserIds can be deleted!");
		}
		/*
		else if (remoteUserId instanceof OpenIdRemoteUserId) {
			this.deleteOpenIdUserId(new OpenIdRemoteUserParam(null, remoteUserId), session);
		}
		else if (remoteUserId instanceof LdapRemoteUserId) {
			this.deleteLdapUserId(new LdapRemoteUserParam(null, remoteUserId), session);
		}
		*/
	}
	
	private void deleteSamlUserId(final SamlUserParam samlUserParam, final DBSession session) {
		this.delete("deleteSamlUserId", samlUserParam, session);
	}
	/*
	private void deleteOpenIdUserId(OpenIdRemoteUserParam remoteUserParam, DBSession session) {
		this.delete("deleteOpenIdUserId", remoteUserParam, session);
	}
	private void deleteLdapUserId(LdapRemoteUserParam remoteUserParam, DBSession session) {
		this.delete("deleteLdapUserId", remoteUserParam, session);
	}
	*/
	
	/**
	 * Deletes all RemoteUserIds from all remoteUser tables (currently only saml) for the given userName
	 * @param userName
	 * @param session
	 */
	private void deleteRemoteUser(final String userName, final DBSession session) {
		this.delete("deleteSamlUserIds", userName, session);
	}
	
	/**
	 * Updates RemoteUserIds of user (currently only saml)
	 * ToDo - Make this a generic method and implement this outside the database-module
	 * @param user
	 * @param session
	 */
	private void updateRemoteUser(final User existingUser, final User user, final DBSession session) {
		//Update and add new RemoteIds
		/*
		boolean addedNewRemoteIds = false;
		for (RemoteUserId user_ruid : user.getRemoteUserIds()) {
			SamlRemoteUserId user_sruid = (SamlRemoteUserId) user_ruid;
			boolean addNewRemoteId = true;
			for (RemoteUserId existUser_ruid : existingUser.getRemoteUserIds()) {
				SamlRemoteUserId existUser_sruid = (SamlRemoteUserId) existUser_ruid;
				if (user_sruid.getIdentityProviderId().equals(existUser_sruid.getIdentityProviderId())) {
					if (!user_sruid.getUserId().equals(existUser_sruid.getUserId())) {
						//Update UserId
						//Todo - Implement update sql query
						this.deleteRemoteUserId(existUser_sruid, session);
						this.insertRemoteUserId(user, user_sruid, session);
						addNewRemoteId = false;
					} else {
						//RemoteId exists
						addNewRemoteId = false;
					}
					break;
				}
				else if (user_sruid.getUserId().equals(existUser_sruid.getUserId())) {
					//Update identity_provider
					//Todo - Implement update sql query
					this.deleteRemoteUserId(existUser_sruid, session);
					this.insertRemoteUserId(user, user_sruid, session);
					addNewRemoteId = false;
					break;
				}
			}
			if(addNewRemoteId) {
				//SamlRemoteId does not exist -> Add new SamlRemoteId
				this.insertRemoteUserId(user, user_sruid, session);
				addedNewRemoteIds = true;
			}
		}
		
		//Delete non existing ruids
		if(addedNewRemoteIds || existingUser.getRemoteUserIds().size() > user.getRemoteUserIds().size()) {
			User updatedExistingUser = this.getUserDetails(existingUser.getName(), session);
			for (RemoteUserId existUser_ruid : updatedExistingUser.getRemoteUserIds()) {
				SamlRemoteUserId existUser_sruid = (SamlRemoteUserId) existUser_ruid;
				boolean deleteRemoteId = true;
				for (RemoteUserId user_ruid : user.getRemoteUserIds()) {
					SamlRemoteUserId user_sruid = (SamlRemoteUserId) user_ruid;
					if (existUser_sruid.getUserId().equals(user_sruid.getUserId())) {
						deleteRemoteId = false;
						break;
					}
				}
				if(deleteRemoteId) {
					this.deleteRemoteUserId(existUser_sruid, session);
				}
			}
		}
		*/
		
		// TODO: log remote ids?
		this.deleteRemoteUser(user.getName(), session);
		for (final RemoteUserId remoteUserId : user.getRemoteUserIds()) {
			this.insertRemoteUserId(user, remoteUserId, session);
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
	 * Deletes a user from the openID table
	 * 
	 * @param user user authenticating via OpenID
	 * @param session
	 */
	public void deleteOpenIDUser(final String userName, final DBSession session) {
		this.delete("deleteOpenIDUser", userName, session);
	}
	
	/**
	 * Deletes a user from the ldapUser table
	 * 
	 * @param user user authenticating via ldap
	 * @param session
	 */
	private void deleteLdapUserId(final String userName, final DBSession session) {
		this.delete("deleteLdapUser", userName, session);
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
			//Update RemoteUserIds
			this.updateRemoteUser(existingUser, user, session);
			
			// update user (does not incl. userSettings)
			UserUtils.updateUser(existingUser, user);
			
			this.checkUser(existingUser, session);
			
			this.plugins.onUserUpdate(existingUser.getName(), session);
			
			/*
			 * FIXME: OpenID and LdapId and RemoteId (saml) were updated in existingUser
			 * but the current "updateUser" Statement will leave those fields unchanged in the database
			 */
			this.update("updateUser", existingUser, session);
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
		
		return user.getName();
	}


	/**
	 * Delete a pending user.
	 * @param username
	 * @param session
	 */
    public void deletePendingUser(final String username, final DBSession session) {
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
		session.beginTransaction();
		try {
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
			user.setPasswordSalt(null);
			this.plugins.onUserDelete(userName, session);
			
			this.updateUser(user, session);
			
			/*
			 * before deleting user remove it from all non-special groups
			 */
			final List<Group> groups = groupDBManager.getGroupsForUser(userName, true, session);
			for (final Group group: groups){
				groupDBManager.removeUserFromGroup(group.getName(), userName, session);
			}
			
			/*
			 * We remove the user's open ID and LDAP entry from the corresponding table. 
			 * Otherwise, a new registration with that ID is not possible.
			 */
			this.deleteLdapUserId(user.getName(), session);
			this.deleteOpenIDUser(user.getName(), session);
			//Delete RemoteUserId
			this.deleteRemoteUser(user.getName(), session);
			/*
			 * flag user as spammer & all his posts as spam
			 */
			user.setAlgorithm("self_deleted");
			this.adminDBManager.flagSpammer(user, AdminDatabaseManager.DELETED_UPDATED_BY, session);
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
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
		if ((foundUser.getName() != null) && !foundUser.isSpammer() && (foundUser.getApiKey() != null) && foundUser.getApiKey().equals(apiKey)) {
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
	 * Retrieve a list of related users by folkrank for a given list of tags
	 * 
	 * @param tagIndex - the list of tags (as tag index)
	 * @param limit
	 * @param offset
	 * @param session
	 * @return a list of users, related by folkrank for a given list of tags
	 */
	public List<User> getRelatedUsersByFolkrankAndTags(final List<TagIndex> tagIndex, final int limit, final int offset, final DBSession session) {
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
	 * Gets a username by RemoteUserId
	 * @param remoteUserId
	 * @param session
	 * @return username
	 */
	public String getUsernameByRemoteUser(final RemoteUserId remoteUserId, final DBSession session) {
		if (remoteUserId instanceof SamlRemoteUserId) {
			return this.queryForObject("getUsernameBySamlRemoteUserId", new SamlUserParam(null, (SamlRemoteUserId)remoteUserId), String.class, session);
		} else {
			throw new IllegalArgumentException("Only SamlRemoteUserIds can be retrieved!");
		}
		/*
		else if (remoteUserId instanceof OpenIdRemoteUserId) {
			return this.queryForObject("getUsernameByOpenIdRemoteUserId", new OpenIdRemoteUserParam(null, remoteUserId), String.class, session);
		}
		else if (remoteUserId instanceof LdapRemoteUserId) {
			return this.queryForObject("getUsernameByLdapRemoteUserId", new LdapRemoteUserParam(null, remoteUserId), String.class, session);
		}
		*/
	}
	
	/**
	 * ToDo - Make this a more generic Method
	 * @param userName
	 * @param session
	 * @return List<SamlRemoteUserParam>
	 */
	private List<SamlRemoteUserId> getSamlRemoteUserIds(final String userName, final DBSession session) {
		return this.queryForList("getSamlRemoteUserIds", userName, SamlRemoteUserId.class, session);
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
	 * @param tag this parameter allows users to label others, e.g., for
	 * annotating interests groups - but using system tags this attribute
	 * is also used to label users which were imported from other sites 
	 * (e.g. 'sys:network:facebook')
	 * @param session 
	 * result: (sourceUser, targetUser) \in relation
	 */
	public void createUserRelation(final String sourceUser, final String targetUser, final UserRelation relation, final String tag, final DBSession session) {
		final UserParam param = new UserParam();
		param.setUserName(sourceUser);
		param.setRequestedUserName(targetUser);
		
		switch (relation) {
			case FOLLOWER_OF:
				/*
				 *  sourceUser will now follow targetUser therefore
				 *  (sourceUser, targetUser)\in FOLLOWER_OF
				 *  
				 */
				if (present(tag)) {
					// labeling of user relations is only allowed for
					// friendship relations
					throw new UnsupportedRelationException();
				}
				break;
			case OF_FRIEND:
				if (present(tag)) {
					// restrict to users labeled with the given tag, if present
					param.setTag(new Tag(tag));
				} else {
					param.setTag(BIBSONOMY_FRIEND_SYSTEM_TAG);
				}
				// TODO: should we introduce network_user_ids???
				break;
			case SPAMMER:
				param.setTag(BIBSONOMY_SPAMMER_SYSTEM_TAG);
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
		
		this.insert("insertRelation_" + relation.toString(), param, session);
	}
	
	/**
	 * Get every User, that is in a specified relation with a given user:
	 * 
	 * @param sourceUser - user on the left side of the relation
	 * @param relation - currently available: FOLLOWER_OF, OF_FOLLOWER, OF_FRIEND, FRIEND_OF
	 * @param tag this parameter allows users to label others, e.g., for
	 * annotating interests groups - but using system tags this attribute
	 * is also used to label users which were imported from other sites 
	 * (e.g. 'sys:network:facebook')
	 * @param session 
	 * @return as List all users, that are in relation with the sourceUser i.e. All users u such that (sourceUser, u)\in relation
	 */
	public List<User> getUserRelation(final String sourceUser, final UserRelation relation, final String tag, final DBSession session) {
		final UserParam param = new UserParam();
		param.setUserName(sourceUser);
		
		switch (relation) {
		case FOLLOWER_OF:
			/*
			 * get all Users, that the sourceUser follows
			 */
	    	// handled with the following 'OF_FOLLOWER' case
		case OF_FOLLOWER:
			/*
			 * get all users, that follow the sourceUser
			 */
		    if (present(tag)) {
		        // labeling of user relations is only allowed for
		        // friendship relations
		        throw new UnsupportedRelationException();
		    }
			break;
		case SPAMMER:
			/*
			 * get all users, that sourceUser has tagged as spammer
			 */
			param.setTag(BIBSONOMY_SPAMMER_SYSTEM_TAG);
			break;
		case OF_FRIEND:
			/*
			 *  get all users, that sourceUser has in his friends list			 
			 */
		    // handled with the following 'FRIEND_OF' case
		case FRIEND_OF:
			/*
			 * get all users, that have sourceUser in their friends list
			 */
			this.handleTaggedRelationship(tag, param);
			break;
		default:
			/*
			 * Other relations are not supported at this time 
			 */
			throw new UnsupportedRelationException();
		}
		
		//list of all targetUsers for sourceUser in (the) relation
		return this.queryForList("getRelation_" + relation.toString(), param, User.class, session);
	}
	
	/**
	 * Delete a relation between two users (if present)
	 * 
	 * @param sourceUser (left side of the relation)
	 * @param targetUser (right side of the relation)
	 * @param relation currently available: FOLLOWER_OF, OF_FRIEND 
	 * (their duals not included intentionally, since these relations 
	 * should only be modified by the targetUser (right hand side of the relation) 
	 * @param tag this parameter allows users to label others, e.g., for
	 * annotating interests groups - but using system tags this attribute
	 * is also used to label users which were imported from other sites 
	 * (e.g. 'sys:network:facebook')
	 * @param session 
	 * result: (sourceUser, targetUser) \notin relation
	 */
	public void deleteUserRelation(final String sourceUser, final String targetUser, final UserRelation relation, final String tag, final DBSession session) {
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
			    if (present(tag)) {
			        // labeling of user relations is only allowed for
			        // friendship relations
			        throw new UnsupportedRelationException();
			    }
				this.plugins.onDeleteFellowship(param, session);
				break;
			case OF_FRIEND:
				/* 
				 * this means: sourceUser will no longer have targetUser as friend and therefore 
				 * delete (sourceUser, targetUser) from  OF_FRIEND
				 * = targetUser is no longer a friend of sourceUser = targetUser is no longer in sourceUser's friendsList
				 */
				this.handleTaggedRelationship(tag, param);
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

	private void handleTaggedRelationship(final String tag, final UserParam param) {
		if (present(tag)) {
		    // restrict to users labeled with the given tag, if present
		    param.setTag(new Tag(tag));
		}
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
	public List<User> getRelatedUsersByFolkrankAndUser(final String requestedUsername,
			final String loginUserName, final int limit, final int offset, final DBSession session) {
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
		return this.chain.perform(param, session);
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
		final List<User> followingUsers = this.getUserRelation(possibleFollower.getName(), UserRelation.FOLLOWER_OF, null, session);		
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
    	try {
	    	session.beginTransaction();
	        this.performActivationSteps(user, session);
			session.commitTransaction();
    	} finally {
    		session.endTransaction();
    	}
        return user.getName();
    }
	
	/**
	 * Small wrapper to make these steps usable in GroupDatabaseManager.
	 * @param user
	 * @param session 
	 */
	protected void performActivationSteps(final User user, final DBSession session) {
		this.insert("activateUser", user.getName(), session);
		this.deletePendingUser(user.getName(), session);
		this.insertDefaultWiki(user, session);
	}

    /**
     * Inserts a default wiki for a newly activated user or for a newly
     * registered openid user.
     * 
     * @param user 
     * @param session
     */
	private void insertDefaultWiki(final User user, final DBSession session) {
		final WikiParam param = new WikiParam();
		param.setUserName(user.getName());
		param.setDate(user.getRegistrationDate());

		// Hier wird standardmaessig ein Benutzer-Wiki angelegt!
		param.setWikiText(TemplateManager.getTemplate("user1en"));
		// hier passiert keine Sicherung, da die session-transactions in den umfassenden
		// Methoden bereits eroeffnet wurden.
		this.insert("insertWiki", param, session);
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
		final UserParam param = new UserParam();
		param.setOffset(start);
		param.setLimit(end);
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
		if (search == null) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Cannot execute query getPendingUserByActivationCode without activation code given!");
		}
		final UserParam param = new UserParam();
		param.setOffset(start);
		param.setLimit(end);
		param.setSearch(search);
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
		final UserParam param = new UserParam();
		param.setOffset(start);
		param.setLimit(end);
		param.setRequestedGroupName(username);
        return this.queryForList("getPendingUserByUsername", param, User.class, session);
    }	
    
    /**
     * returns all users with a username starting with searchString
     * 
     * @param searchString
     * @param limit
     * @param session
     * @return list of users
     */
    public List<User> getUsersBySearch(final String searchString, final int limit, final DBSession session) {
    	final UserParam param = new UserParam();
    	param.setSearch(searchString);
    	param.setLimit(limit);
    	return this.queryForList("getUsersBySearch", param, User.class, session);
    }
	
	/**
	 * @param session
	 * @return
	 */
	public int getFriendsInHistoryCount(DBSession session) {
		final Integer result = this.queryForObject("getFriendHistoryCount", Integer.class, session);
		return result == null ? 0 : result.intValue();
	}

	/**
	 * @param chain the chain to set
	 */
	public void setChain(final Chain<List<User>, UserParam> chain) {
		this.chain = chain;
	}

	/**
	 * @param validator the validator to set
	 */
	public void setValidator(final DatabaseModelValidator<User> validator) {
		this.validator = validator;
	}
	/**
	 * @param fileLogic the fileLogic to set
	 */
	public void setFileLogic(final FileLogic fileLogic) {
		this.fileLogic = fileLogic;
	}

	/**
	 * @param usersDefaultToClassify the usersDefaultToClassify to set
	 */
	public void setUsersDefaultToClassify(Integer usersDefaultToClassify) {
		this.usersDefaultToClassify = usersDefaultToClassify;
	}
}