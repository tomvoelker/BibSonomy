package org.bibsonomy.search.index.database;

import org.bibsonomy.search.update.SearchIndexSyncState;

/**
 * logic to get information for the database state
 * @author dzo
 */
public interface DatabaseInformationLogic<S extends SearchIndexSyncState> {

	/**
	 * @return the current database state
	 */
	S getDbState();
}
