package org.bibsonomy.database.plugin;

import org.bibsonomy.database.util.DBSession;

/**
 * This interface supplies hooks which can be implemented by plugins. This way
 * the code for basic operations, like updating a bookmark or publication, can
 * be kept concise and is easier to maintain.<br/>
 * 
 * If a method returns <code>null</code> its execution will be skipped.
 * 
 * @author Jens Illig
 * @author Christian Schenk
 * @author Anton Wilhelm
 * @version $Id$
 */
public interface DatabasePlugin {

	public Runnable onBibTexInsert(int contentId, DBSession session);

	public Runnable onBibTexDelete(int contentId, DBSession session);

	public Runnable onBibTexUpdate(int newContentId, int contentId, DBSession session);

	public Runnable onBookmarkInsert(int contentId, DBSession session);

	public Runnable onBookmarkDelete(int contentId, DBSession session);

	public Runnable onBookmarkUpdate(int newNontentId, int contentId, DBSession session);

	public Runnable onTagRelationDelete(String upperTagName, String lowerTagName, String userName, DBSession session);

	public Runnable onTagDelete(int contentId, DBSession session);
	
	public Runnable onUserInsert(String userName, DBSession session);

	public Runnable onUserDelete(String userName, DBSession session);

	public Runnable onUserUpdate(String userName, DBSession session);	

	public Runnable onRemoveUserFromGroup(String userName, int groupId, DBSession session);
}