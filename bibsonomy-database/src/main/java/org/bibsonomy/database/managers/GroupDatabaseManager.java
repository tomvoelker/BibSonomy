package org.bibsonomy.database.managers;

import java.util.List;

import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.params.GroupParam;
import org.bibsonomy.model.Group;

/**
 * Used to retrieve groups from the database.
 *
 * @author Christian Schenk
 */
public class GroupDatabaseManager extends AbstractDatabaseManager  {

	/** Singleton */
	private final static GroupDatabaseManager singleton = new GroupDatabaseManager();

	GroupDatabaseManager() {
	}

	public static GroupDatabaseManager getInstance() {
		return singleton;
	}

	/**
	 * Returns a list of all groups
	 */
	public List<Group> getAllGroups() {
		return this.queryForList("getAllGroups", null, Group.class, null);
	}

	/**
	 * Returns a specific group
	 */
	public Group getGroupByName(final GroupParam param) {
		return this.queryForObject("getGroupByName", param, Group.class, null);
	}

	/**
	 * Returns a group with all its memebers
	 */
	public Group getGroupMembers(final GroupParam param) {
		return this.queryForObject("getGroupMembers", param, Group.class, null);
	}
}