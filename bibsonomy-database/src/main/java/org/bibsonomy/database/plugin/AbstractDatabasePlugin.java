package org.bibsonomy.database.plugin;

import org.bibsonomy.model.Bookmark;

/**
 * This class should be used by plugins. This way they don't have to implement
 * all methods from the interface DatabasePlugin.
 * 
 * @author Christian Schenk
 */
public class AbstractDatabasePlugin implements DatabasePlugin {

	public Runnable onBookmarkCreate(Bookmark bookmark) {
		return null;
	}

	public Runnable onBookmarkUpdate(Bookmark bookmark, Bookmark oldBookmark) {
		return null;
	}
}