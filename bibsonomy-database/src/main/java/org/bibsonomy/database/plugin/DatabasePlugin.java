package org.bibsonomy.database.plugin;

import org.bibsonomy.database.util.Transaction;

/**
 * This interface supplies hooks which can be implemented by plugins. This way
 * the code for basic operations, like updating a bookmark or publication, can
 * be kept concise and is easier to maintain.<br/>
 * 
 * If a method returns <code>null</code> its execution will be skipped.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public interface DatabasePlugin {

	public Runnable onBibTexInsert(final int contentId, final Transaction session);

	public Runnable onBibTexUpdate(final int newContentId, final int contentId, final Transaction session);
}