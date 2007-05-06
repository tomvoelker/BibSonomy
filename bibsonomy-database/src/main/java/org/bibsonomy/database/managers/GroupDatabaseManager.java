package org.bibsonomy.database.managers;

import java.util.List;

import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.params.GroupParam;
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
	public List<Group> getAllGroups(final Transaction transaction) {
		return this.queryForList("getAllGroups", null, Group.class, transaction);
	}

	/**
	 * Returns a specific group
	 */
	public Group getGroupByName(final GroupParam param, final Transaction transaction) {
		return this.queryForObject("getGroupByName", param, Group.class, transaction);
	}

	/**
	 * Returns a group with all its memebers
	 */
	public Group getGroupMembers(final GroupParam param, final Transaction transaction) {
		return this.queryForObject("getGroupMembers", param, Group.class, transaction);
	}

	/**
	 * Returns a a list of groups for a given user
	 */
	public List<Group> getGroupsForUser(final GroupParam param, final Transaction transaction) {
		return this.queryForList("getGroupsForUser", param, Group.class, transaction);
	}
}