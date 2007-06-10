package org.bibsonomy.database.managers;

import java.util.Collections;
import java.util.List;

import org.bibsonomy.common.enums.Privlevel;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.params.GroupParam;
import org.bibsonomy.database.util.LogicInterfaceHelper;
import org.bibsonomy.database.util.Transaction;
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

	/** Singleton */
	private final static GroupDatabaseManager singleton = new GroupDatabaseManager();
	private final UserDatabaseManager userDb;

	private GroupDatabaseManager() {
		this.userDb = UserDatabaseManager.getInstance();
	}

	public static GroupDatabaseManager getInstance() {
		return singleton;
	}

	/**
	 * Returns a list of all groups
	 */
	public List<Group> getAllGroups(final int start, final int end, final Transaction session) {
		final GroupParam param = LogicInterfaceHelper.buildParam(GroupParam.class, null, null, null, null, null, null, start, end);
		return this.queryForList("getAllGroups", param, Group.class, session);
	}

	/**
	 * Returns a specific group
	 */
	public Group getGroupByName(final String groupname, final Transaction session) {
		if (groupname == null || groupname.trim().length() == 0) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Groupname isn't present");
		}
		return this.queryForObject("getGroupByName", groupname, Group.class, session);
	}

	/**
	 * Returns a group with all its members if the user is allowed to see them.
	 */
	public Group getGroupMembers(final String authUser, final String groupname, final Transaction session) {
		final int groupId = this.getGroupByName(groupname, session).getGroupId();
		final int privlevel = this.getPrivlevelForGroup(groupId, session);
		final Group group = this.queryForObject("getGroupMembers", groupname, Group.class, session);
		// remove members as necessary
		switch (Privlevel.getPrivlevel(privlevel)) {
		case MEMBERS:
			// if the user isn't a member of the group he can't see other
			// members -> and we'll fall through to HIDDEN
			final List<Group> userGroups = this.getGroupsForUser(authUser, session);
			if (this.containsGroupWithName(userGroups, groupname)) break;
		case HIDDEN:
			group.setUsers(Collections.<User> emptyList());
			break;
		}
		return group;
	}

	/**
	 * Returns the privlevel for a group.
	 */
	private Integer getPrivlevelForGroup(final int groupId, final Transaction session) {
		return this.queryForObject("getPrivlevelForGroup", groupId, Integer.class, session);
	}

	/**
	 * Returns a a list of groups for a given user
	 */
	public List<Group> getGroupsForUser(final String username, final Transaction session) {
		return this.queryForList("getGroupsForUser", username, Group.class, session);
	}

	/**
	 * Returns true if a group with the given name is contained in the list,
	 * otherwise false.
	 */
	private boolean containsGroupWithName(final List<Group> groups, final String groupname) {
		for (final Group group : groups) {
			if (groupname.equals(group.getName())) return true;
		}
		return false;
	}

	/**
	 * Stores a group in the database.
	 * 
	 * FIXME: update isn't implemented.
	 */
	public void storeGroup(final Group group, final boolean update, final Transaction session) {
		if (update) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Not implemented yet");
		}

		// check if a user exists with that name
		if (this.userDb.getUserDetails(group.getName(), session) == null) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "There's no user with this name - can't create a group with this name");
		}
		// check if a group exists with that name
		if (this.getGroupByName(group.getName(), session) != null) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "There's already a group with this name");
		}

		this.insertGroup(group, session);
	}

	/**
	 * Inserts a group.
	 */
	private void insertGroup(final Group group, final Transaction session) {
		final int newGroupId = this.getNewGroupId(session);
		group.setGroupId(newGroupId);
		this.insert("insertGroup", group, session);
		this.addUserToGroup(group.getName(), group.getName(), session);
	}

	/**
	 * Adds a user to a group.
	 */
	public void addUserToGroup(final String groupname, final String username, final Transaction session) {
		// check if a user exists with that name
		if (this.userDb.getUserDetails(username, session) == null) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "There's no user with this name");
		}
		final Group group = this.getGroupByName(groupname, session);
		if (group == null) {
			ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Group doesn't exist - can't add user to nonexistent group");
		}
		// XXX: the next line is semantically incorrect
		group.setName(username);
		this.insert("insertUserIntoGroup", group, session);
	}

	/**
	 * Returns a new groupId.
	 */
	private int getNewGroupId(final Transaction session) {
		return this.queryForObject("getNewGroupId", null, Integer.class, session);
	}
}