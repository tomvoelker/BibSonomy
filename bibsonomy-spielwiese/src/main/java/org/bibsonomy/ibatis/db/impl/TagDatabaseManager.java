package org.bibsonomy.ibatis.db.impl;

import java.util.List;

import org.bibsonomy.ibatis.db.AbstractDatabaseManager;
import org.bibsonomy.model.Tag;

/**
 * Used to retrieve Tags from the database.
 * 
 * @author Christian Schenk
 */
public class TagDatabaseManager extends AbstractDatabaseManager {

	/**
	 * Reduce visibility so only the {@link DatabaseManager} can instantiate
	 * this class.
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