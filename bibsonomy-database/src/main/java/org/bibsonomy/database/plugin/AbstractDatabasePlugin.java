package org.bibsonomy.database.plugin;

import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.util.DBSession;

/**
 * This class should be used by plugins. This way they don't have to implement
 * all methods from the interface DatabasePlugin. Furthermore they have access
 * to some basic database methods.
 * 
 * @author Jens Illig
 * @author Christian Schenk
 * @version $Id$
 */
public class AbstractDatabasePlugin extends AbstractDatabaseManager implements DatabasePlugin {

	public Runnable onBibTexInsert(final int contentId, final DBSession session) {
		return null;
	}

	public Runnable onBibTexUpdate(final int newContentId, final int contentId, final DBSession session) {
		return null;
	}

	public Runnable onTagRelationDelete(final String upperTagName, final String lowerTagName, final String userName, final DBSession session) {
		return null;
	}

	public Runnable onBookmarkUpdate(int newContentId, int contentId, DBSession session) {
		// TODO Auto-generated method stub
		return null;
	}

	public Runnable onBookmarkInsert(int contentId, DBSession session) {
		
		return null;
	}
}