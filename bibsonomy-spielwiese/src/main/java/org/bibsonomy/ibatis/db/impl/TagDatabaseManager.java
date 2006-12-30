package org.bibsonomy.ibatis.db.impl;

import java.util.List;

import org.bibsonomy.ibatis.db.AbstractDatabaseManager;
import org.bibsonomy.model.Tag;

public class TagDatabaseManager extends AbstractDatabaseManager {

	/**
	 * Reduce visibility so only the {@link DatabaseManager} can instantiate us.
	 */
	TagDatabaseManager() {
	}

	public Tag getTagById(final int param) {
		return (Tag) this.queryForObject("getTagById", param);
	}

	public List<Tag> getTagByCount(final int param) {
		return this.tagList("getTagByCount", param);
	}
}