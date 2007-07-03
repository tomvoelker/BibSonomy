package org.bibsonomy.database.plugin;

import org.bibsonomy.database.util.DBSession;

/**
 * This interface supplies hooks which can be implemented by plugins. This way
 * the code for basic operations, like updating a bookmark or publication, can
 * be kept concise and is easier to maintain.<br/>
 * 
 * If a method returns <code>null</code> its execution will be skipped.
 * @author mgr
 * @author Jens Illig
 * @author Christian Schenk
 * @version $Id$
 */
public interface DatabasePlugin {

	public Runnable onBibTexInsert(int contentId, DBSession session);

	public Runnable onBibTexUpdate(int newContentId, int contentId, DBSession session);

	public Runnable onBookmarkInsert(int contentId, DBSession session);
	
	public Runnable onBookmarkUpdate(int newContentId, int contentId, DBSession session);
	
	public Runnable onTagRelationDelete(String upperTagName, String lowerTagName, String userName, DBSession session);
}