package org.bibsonomy.ibatis.db.impl;

import org.bibsonomy.ibatis.db.AbstractDatabaseManager;
import org.bibsonomy.ibatis.params.GenericParam;

public class GeneralDatabaseManager extends AbstractDatabaseManager {

	/**
	 * Reduce visibility so only the {@link DatabaseManager} can instantiate us.
	 */
	GeneralDatabaseManager() {
	}

	public boolean isFriendOf(final GenericParam param) {
		return (Boolean) this.queryForObject("isFriendOf", param);
	}
}