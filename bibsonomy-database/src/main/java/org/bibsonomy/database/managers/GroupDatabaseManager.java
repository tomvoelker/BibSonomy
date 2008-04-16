package org.bibsonomy.database.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
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
	private final List<Group> specialGroups;

	private GroupDatabaseManager() {
		this.userDb = UserDatabaseManager.getInstance();
		this.plugins = DatabasePluginRegistry.getInstance();
		this.specialGroups = new ArrayList<Group>();
		this.specialGroups.add(getPrivateGroup());
		this.specialGroups.add(getPublicGroup());
		this.specialGroups.add(getFriendsGroup());
	}

	/**
	 * @return GroupDatabaseManager
	 */
	public static GroupDatabaseManager getInstance() {
		return singleton;
	}

	/**
	 * Returns a list of all groups
	 * @param start 
	 * @param end 
	 * @param session 
	 * @return a list of all groups
	 */
	public List<Group> getAllGroups(final int start, final int end, final DBSession session) {
		final GroupParam param = LogicInterfaceHelper.buildParam(GroupParam.class, null, null, null, null, null, null, start, end, null, null);
		return this.queryForList("getAllGroups", param, Group.class, session);
	}

	/**
	 * Returns a specific group
	 * 
	 * @param groupname 
	 * @param session 
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
		priv.setDescription("private group");
		priv.setGroupId(GroupID.PRIVATE.getId());
		priv.setName("private");
		priv.setPrivlevel(Privlevel.HIDDEN);
		return priv;
	}

	private static Group getFriendsGroup() {
		final Group friends = new Group();
		friends.setDescription("group of all your bibsonomy-friends");
		friends.setGroupId(GroupID.FRIENDS.getId());
		friends.setName("friends");
		friends.setPrivlevel(Privlevel.HIDDEN);
		return friends;
	}
	
	private static Group getInvalidGroup() {
		final Group invalid = new Group();
		invalid.setDescription("invalid group");
		invalid.setGroupId(GroupID.INVALID.getId());
		invalid.setName("invalid");
		invalid.setPrivlevel(Privlevel.HIDDEN);
		return invalid;		
	}
	
	/**
	 * Returns a list with all special groups of the system
	 * 
	 * @return a list with all special groups of the system
	 */
	public List<Group> getSpecialGroups() {
		return this.specialGroups;
	}
	
	/**
	 * Helper function to remove special groups from a List of groups
	 * 
	 * @param groups a list of groups 
	 * @return a new list of groups with special groups removed
	 */
	public List<Group> removeSpecialGroups(final List<Group> groups) {
		final ArrayList<Group> newGroups = new ArrayList<Group>();
		for (final Group group : groups) {
			if (!GroupID.isSpecialGroupId(group.getGroupId()))
				newGroups.add(group);
		}
		return newGroups;
	}

	/**
	 * Returns a group with all its members if the user is allowed to see them.
	 * 
	 * @param authUser 
	 * @param groupname 
	 * @param session 
	 * @return group 
	 */
	public Group getGroupMembers(final String authUser, final String groupname, final DBSession session) {
		log.debug("getGroupMembers " + groupname);
		Group group;
		if ("friends".equals(groupname) == true) {
			group = getFriendsGroup();
			group.setUsers(getFriendsOfUser(authUser,session));
			return group;
		}
		if ("public".equals(groupname)) {
			group = getPrivateGroup();
			group.setUsers(Collections.<User> emptyList());
			return group;
		}
		if ("private".equals(groupname)) {
			group = getPublicGroup();
			group.setUsers(Collections.<User> emptyList());
			return group;
		}

		group = this.queryForObject("getGroupMembers", groupname, Group.class, session);
		if (group == null) {
			log.debug("group " + groupname + " does not exist");
			group = getInvalidGroup();
			group.setUsers(Collections.<User> emptyList());
			return group;
		}

		final int groupId = this.getGroupByName(groupname, session).getGroupId();
		final Privlevel privlevel = this.getPrivlevelForGroup(groupId, session);
		// remove members as necessary
		switch (privlevel) {
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

	/**
	 * @param authUser
	 * @param session
	 * @return a list of users
	 */
	@SuppressWarnings("unchecked")
	public List<User> getFriendsOfUser(final String authUser, final DBSession session) {
		return queryForList("getFriendsOfUser", authUser, session);
	}

	/**
	 * Returns the privlevel for a group.
	 */
	private Privlevel getPrivlevelForGroup(final int groupId, final DBSession session) {
		return this.queryForObject("getPrivlevelForGroup", groupId, Privlevel.class, session);
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
	 * 
	 * @param username 
	 * @param session 
	 * @return a list of groups
	 */
	public List<Group> getGroupsForUser(final String username, final DBSession session) {
		return this.queryForList("getGroupsForUser", username, Group.class, session);
	}
	
	/**
	 * get all groups a user is member of, with or without special groups
	 * 
	 * @param userName
	 * @param removeSpecialGroups
	 * @param session
	 * @return a list of groups the user is member of
	 */
	public List<Group> getGroupsForUser(final String userName, final boolean removeSpecialGroups, final DBSession session) {
		if (removeSpecialGroups) {
			return this.removeSpecialGroups(this.getGroupsForUser(userName, session));
		}
		return this.getGroupsForUser(userName, session);
	}
	
	/** Gets all groups in which both user A and user B are in. 
	 * 
	 * @param userNameA - name of the first user.
	 * @param userNameB - name of the second user.
	 * @param session
	 * @return The list of groups both given users are in.
	 */
	public List<Group> getCommonGroups(final String userNameA, final String userNameB, final DBSession session) {
		final List<Group> userAGroups = this.getGroupsForUser(userNameA, true, session);
		final List<Group> userBGroups = this.getGroupsForUser(userNameB, true, session);
	
		/*
		 * It is not very efficient, to do this in two cascaded loops, but users are 
		 * typically in very few groups and with linked lists there is probably no much
		 * more efficient way to do it. 
		 */
		final List<Group> commonGroups = new LinkedList<Group>();
		for (final Group a:userAGroups) {
			for (final Group b:userBGroups) {
				if (a.getGroupId() == b.getGroupId()) {
					commonGroups.add(a);
				}
			}
		}
		return commonGroups;
	}
	
	/**
	 * Returns a a list of groups for a given content ID
	 * 
	 * @param contentId 
	 * @param session 
	 * @return a list of groups
	 */
	public List<Group> getGroupsForContentId(final Integer contentId, final DBSession session) {
		return this.queryForList("getGroupsForContentId", contentId, Group.class, session);
	}	

	/**
	 * Stores a group in the database.
	 * 
	 * FIXME: update isn't implemented.
	 * 
	 * @param group 
	 * @param update 
	 * @param session 
	 */
	public void storeGroup(final Group group, final boolean update, final DBSession session) {
		if (update) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Not implemented yet");
		}

		// check if a user exists with that name
		if (this.userDb.getUserDetails(group.getName(), session).getName() == null) {
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
	 * 
	 * @param groupname 
	 * @param session 
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
	 * 
	 * @param groupname 
	 * @param username 
	 * @param session 
	 */
	public void addUserToGroup(final String groupname, final String username, final DBSession session) {
		// check if a user exists with that name
		if (this.userDb.getUserDetails(username, session).getName() == null) {
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
	 * 
	 * @param groupname 
	 * @param username 
	 * @param session 
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