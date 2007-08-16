package org.bibsonomy.database.managers;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.Privlevel;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.params.GroupParam;
import org.bibsonomy.database.plugin.DatabasePluginRegistry;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.database.util.LogicInterfaceHelper;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.User;
import org.bibsonomy.util.ExceptionUtils;

/**
 * Used to retrieve groups from the database.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public class GroupDatabaseManager extends AbstractDatabaseManager {

	private static final Logger log = Logger.getLogger(GroupDatabaseManager.class);
	
	private final static GroupDatabaseManager singleton = new GroupDatabaseManager();
	private final UserDatabaseManager userDb;
	private final DatabasePluginRegistry plugins;

	private GroupDatabaseManager() {
		this.userDb = UserDatabaseManager.getInstance();
		this.plugins = DatabasePluginRegistry.getInstance();
	}

	public static GroupDatabaseManager getInstance() {
		return singleton;
	}

	/**
	 * Returns a list of all groups
	 */
	public List<Group> getAllGroups(final int start, final int end, final DBSession session) {
		final GroupParam param = LogicInterfaceHelper.buildParam(GroupParam.class, null, null, null, null, null, null, start, end);
		return this.queryForList("getAllGroups", param, Group.class, session);
	}

	/**
	 * Returns a specific group
	 * 
	 * @return Returns a {@link Group} object if the group exists otherwise null.
	 */
	public Group getGroupByName(final String groupname, final DBSession session) {
		if (groupname == null || groupname.trim().length() == 0) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Groupname isn't present");
		}
		if ("friends".equals(groupname) == true) {
			return getFriendsGroup();
		}
		if ("public".equals(groupname) == true) {
			return getPublicGroup();
		}
		if ("private".equals(groupname) == true) {
			return getPrivateGroup();
		}
		return this.queryForObject("getGroupByName", groupname, Group.class, session);
	}

	private static Group getFriendsGroup() {
		final Group friends = new Group();
		friends.setDescription("group of all your bibsonomy-friends");
		friends.setGroupId(GroupID.FRIENDS.getId());
		friends.setName("friends");
		friends.setPrivlevel(Privlevel.HIDDEN);
		return friends;
	}
	
	private static Group getPublicGroup() {
		final Group pub = new Group();
		pub.setDescription("public group");
		pub.setGroupId(GroupID.PUBLIC.getId());
		pub.setName("public");
		pub.setPrivlevel(Privlevel.PUBLIC);
		return pub;
	}
	
	private static Group getPrivateGroup() {
		final Group priv = new Group();
		priv.setDescription("private groiup");
		priv.setGroupId(GroupID.PRIVATE.getId());
		priv.setName("private");
		priv.setPrivlevel(Privlevel.HIDDEN);
		return priv;
	}	

	/**
	 * Returns a group with all its members if the user is allowed to see them.
	 */
	public Group getGroupMembers(final String authUser, final String groupname, final DBSession session) {
		final Group group;
		if ("friends".equals(groupname) == true) {
			group = getFriendsGroup();
			group.setUsers(getFriendsOfUser(authUser,session));
			return group;
		}
		
		group = this.queryForObject("getGroupMembers", groupname, Group.class, session);
		if (group == null) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Group ('" + groupname + "') doesn't exist");
		}
		final int groupId = this.getGroupByName(groupname, session).getGroupId();
		final int privlevel = this.getPrivlevelForGroup(groupId, session);
		// remove members as necessary
		switch (Privlevel.getPrivlevel(privlevel)) {
		case MEMBERS:
			// if the user isn't a member of the group he can't see other
			// members -> and we'll fall through to HIDDEN
			if (this.isUserInGroup(authUser, groupname, session)) break;
		case HIDDEN:
			group.setUsers(Collections.<User> emptyList());
			break;
		}
		return group;
	}

	@SuppressWarnings("unchecked")
	public List<User> getFriendsOfUser(final String authUser, final DBSession session) {
		return queryForList("getFriendsOfUser", authUser, session);
	}

	/**
	 * Returns the privlevel for a group.
	 */
	private Integer getPrivlevelForGroup(final int groupId, final DBSession session) {
		return this.queryForObject("getPrivlevelForGroup", groupId, Integer.class, session);
	}

	/**
	 * Returns true if the user is in the group otherwise false.
	 */
	private boolean isUserInGroup(final String username, final String groupname, final DBSession session) {
		final List<Group> userGroups = this.getGroupsForUser(username, session);
		for (final Group group : userGroups) {
			if (groupname.equals(group.getName())) return true;
		}
		return false;
	}

	/**
	 * Returns a a list of groups for a given user
	 */
	public List<Group> getGroupsForUser(final String username, final DBSession session) {
		return this.queryForList("getGroupsForUser", username, Group.class, session);
	}
	
	/**
	 * Returns a a list of groups for a given content ID
	 */
	public List<Group> getGroupsForContentId(final Integer contentId, final DBSession session) {
		return this.queryForList("getGroupsForContentId", contentId, Group.class, session);
	}	

	/**
	 * Stores a group in the database.
	 * 
	 * FIXME: update isn't implemented.
	 */
	public void storeGroup(final Group group, final boolean update, final DBSession session) {
		if (update) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Not implemented yet");
		}

		// check if a user exists with that name
		if (this.userDb.getUserDetails(group.getName(), session) == null) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "There's no user with this name - can't create a group with this name");
		}
		// check if a group exists with that name
		if (this.getGroupByName(group.getName(), session) != null) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "There's already a group with this name ('" + group.getName() + "')");
		}

		this.insertGroup(group, session);
	}

	/**
	 * Inserts a group.
	 */
	private void insertGroup(final Group group, final DBSession session) {
		final int newGroupId = this.getNewGroupId(session);
		group.setGroupId(newGroupId);
		this.insert("insertGroup", group, session);
		this.addUserToGroup(group.getName(), group.getName(), session);
	}

	/**
	 * Returns a new groupId.
	 */
	private int getNewGroupId(final DBSession session) {
		return this.queryForObject("getNewGroupId", null, Integer.class, session);
	}

	/**
	 * Delete a group from the database.
	 */
	public void deleteGroup(final String groupname, final DBSession session) {
		// make sure that the group exists
		final Group group = this.getGroupByName(groupname, session);
		if (group == null) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Group ('" + groupname + "') doesn't exist");
		}
		this.delete("deleteGroup", group.getGroupId(), session);
		this.delete("removeAllUserFromGroup", group.getGroupId(), session);
	}

	/**
	 * Adds a user to a group.
	 */
	public void addUserToGroup(final String groupname, final String username, final DBSession session) {
		// check if a user exists with that name
		if (this.userDb.getUserDetails(username, session) == null) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "There's no user with this name ('" + username + "')");
		}
		// make sure that the group exists
		final Group group = this.getGroupByName(groupname, session);
		if (group == null) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Group ('" + groupname + "') doesn't exist - can't add user to nonexistent group");
		}
		// make sure that the user isn't a member of the group
		if (this.isUserInGroup(username, groupname, session)) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "User ('" + username + "') is already a member of this group ('" + groupname + "')");
		}
		// XXX: the next line is semantically incorrect
		group.setName(username);
		this.insert("addUserToGroup", group, session);
	}

	/**
	 * Removes a user from a group.
	 */
	public void removeUserFromGroup(final String groupname, final String username, final DBSession session) {
		// make sure that the group exists
		final Group group = this.getGroupByName(groupname, session);
		if (group == null) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Group ('" + groupname + "') doesn't exist - can't remove user from nonexistent group");
		}
		// make sure that the user is a member of the group
		if (this.isUserInGroup(username, groupname, session) == false) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "User ('" + username + "') isn't a member of this group ('" + groupname + "')");
		}
		// XXX: the next line is semantically incorrect
		group.setName(username);
		
		this.plugins.onRemoveUserFromGroup(username, group.getGroupId(), session);
		this.delete("removeUserFromGroup", group, session);
	}
}