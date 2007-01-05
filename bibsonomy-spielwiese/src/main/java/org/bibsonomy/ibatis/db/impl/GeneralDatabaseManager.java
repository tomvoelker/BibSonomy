package org.bibsonomy.ibatis.db.impl;

import java.util.List;

import org.bibsonomy.ibatis.db.AbstractDatabaseManager;
import org.bibsonomy.ibatis.params.GenericParam;

/**
 * Used to retrieve all different kind of stuff from the database.
 * 
 * @author Christian Schenk
 */
public class GeneralDatabaseManager extends AbstractDatabaseManager {

	/**
	 * Reduce visibility so only the {@link DatabaseManager} can instantiate
	 * this class.
	 */
	GeneralDatabaseManager() {
	}

	public boolean isFriendOf(final GenericParam param) {
		return (Boolean) this.queryForObject("isFriendOf", param);
	}

	public List<Integer> getGroupsForUser(final GenericParam param) {
		return this.intList("getGroupsForUser", param);
	}
}