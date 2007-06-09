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

/**
 * Used to retrieve groups from the database.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public class GroupDatabaseManager extends AbstractDatabaseManager {

	/** Singleton */
	private final static GroupDatabaseManager singleton = new GroupDatabaseManager();

	private GroupDatabaseManager() {
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
			if (this.userIsInGroupWithName(groupname, userGroups)) break;
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
	private boolean userIsInGroupWithName(final String groupname, final List<Group> groups) {
		for (final Group group : groups) {
			if (groupname.equals(group.getName())) return true;
		}
		return false;
	}
}