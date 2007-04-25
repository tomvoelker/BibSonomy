package org.bibsonomy.database.managers;

import java.util.List;

import org.bibsonomy.database.AbstractDatabaseManager;
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
}