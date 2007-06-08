package org.bibsonomy.database.managers;

import java.util.List;

import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.params.GroupParam;
import org.bibsonomy.database.util.LogicInterfaceHelper;
import org.bibsonomy.database.util.Transaction;
import org.bibsonomy.model.Group;

/**
 * Used to retrieve groups from the database.
 *
 * @author Christian Schenk
 * @version $Id$
 */
public class GroupDatabaseManager extends AbstractDatabaseManager  {

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
	 * Returns a group with all its memebers
	 */
	public Group getGroupMembers(final String groupname, final Transaction session) {
		return this.queryForObject("getGroupMembers", groupname, Group.class, session);
	}

	/**
	 * Returns a a list of groups for a given user
	 */
	public List<Group> getGroupsForUser(final String username, final Transaction session) {
		return this.queryForList("getGroupsForUser", username, Group.class, session);
	}
}