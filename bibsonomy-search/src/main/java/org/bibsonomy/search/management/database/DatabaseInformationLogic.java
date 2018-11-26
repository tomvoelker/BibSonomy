package org.bibsonomy.search.management.database;

import org.bibsonomy.search.update.SearchIndexSyncState;

/**
 * logic to get information for the database state
 * @author dzo
 */
public interface DatabaseInformationLogic {

	SearchIndexSyncState getDbState();
}
