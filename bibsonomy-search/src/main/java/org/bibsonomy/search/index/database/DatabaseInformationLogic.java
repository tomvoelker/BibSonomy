package org.bibsonomy.search.index.database;

import org.bibsonomy.search.update.DefaultSearchIndexSyncState;

/**
 * logic to get information for the database state
 * @author dzo
 */
public interface DatabaseInformationLogic<S extends DefaultSearchIndexSyncState> {

	S getDbState();
}
