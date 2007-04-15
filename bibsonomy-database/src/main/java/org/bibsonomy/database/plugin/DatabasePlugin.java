package org.bibsonomy.database.plugin;

import org.bibsonomy.model.Bookmark;

/**
 * This interface supplies hooks which can be implemented by plugins. This way
 * the code for basic operations, like updating a bookmark, can be kept concise
 * and is easier to maintain.
 * 
 * @author Christian Schenk
 */
public interface DatabasePlugin {

	public Runnable onBookmarkCreate(final Bookmark bookmark);

	public Runnable onBookmarkUpdate(final Bookmark bookmark, final Bookmark oldBookmark);
}