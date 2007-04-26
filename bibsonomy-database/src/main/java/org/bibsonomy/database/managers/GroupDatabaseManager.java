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

	@SuppressWarnings("unchecked")
	protected List<Group> groupList(final String query, final Group group) {
		return (List<Group>) queryForList(query, group);
	}

	/**
	 * Returns a list of all groups
	 */
	public List<Group> getAllGroups() {
		return this.groupList("getAllGroups", null);
	}

	/**
	 * Returns a specific group
	 */
	public Group getGroupByName(final GroupParam param) {
		return (Group) this.queryForObject("getGroupByName", param);
	}

	/**
	 * Returns a group with all its memebers
	 */
	public Group getGroupMembers(final GroupParam param) {
		return (Group) this.queryForObject("getGroupMembers", param);
	}
}