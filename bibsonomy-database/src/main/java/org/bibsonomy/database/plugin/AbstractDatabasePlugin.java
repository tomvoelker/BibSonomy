package org.bibsonomy.database.plugin;

import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.util.Transaction;

/**
 * This class should be used by plugins. This way they don't have to implement
 * all methods from the interface DatabasePlugin. Furthermore they have access
 * to some basic database methods.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public class AbstractDatabasePlugin extends AbstractDatabaseManager implements DatabasePlugin {

	public Runnable onBibTexInsert(int contentId, Transaction session) {
		return null;
	}

	public Runnable onBibTexUpdate(int newContentId, int contentId, Transaction session) {
		return null;
	}
	
	public Runnable onTagRelationDelete(final String upperTagName, final String lowerTagName, final String userName, final Transaction session) {
		return null;
	}
}