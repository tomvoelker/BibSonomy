package org.bibsonomy.search.index.database.person;

import org.bibsonomy.database.common.AbstractDatabaseManagerWithSessionManagement;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.search.index.database.DatabaseInformationLogic;
import org.bibsonomy.search.update.SearchIndexSyncState;

/**
 * implementation to get dbstate for {@link org.bibsonomy.model.Person} index updates
 *
 * @author dzo
 */
public class PersonDatabaseInformationLogic extends AbstractDatabaseManagerWithSessionManagement implements DatabaseInformationLogic {
	@Override
	public SearchIndexSyncState getDbState() {
		try (final DBSession session = this.openSession()) {
			final SearchIndexSyncState searchIndexSyncState = new SearchIndexSyncState();
			final Integer lastId = this.queryForObject("getLastPersonChangeId", Integer.class, session);
			searchIndexSyncState.setLastPersonChangeId(lastId);
			return searchIndexSyncState;
		}
	}
}
