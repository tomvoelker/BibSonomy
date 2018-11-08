package org.bibsonomy.search.index.database.person;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.database.common.AbstractDatabaseManagerWithSessionManagement;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.search.index.database.DatabaseInformationLogic;
import org.bibsonomy.search.update.DefaultSearchIndexSyncState;

import java.util.Date;

/**
 * implementation to get dbstate for {@link org.bibsonomy.model.Person} index updates
 *
 * @author dzo
 */
public class PersonDatabaseInformationLogic extends AbstractDatabaseManagerWithSessionManagement implements DatabaseInformationLogic<DefaultSearchIndexSyncState> {

	@Override
	public DefaultSearchIndexSyncState getDbState() {
		try (final DBSession session = this.openSession()) {
			final DefaultSearchIndexSyncState searchIndexSyncState = new DefaultSearchIndexSyncState();
			final Integer lastId = this.queryForObject("getLastPersonChangeId", Integer.class, session);
			searchIndexSyncState.setLastPersonChangeId(lastId);
			Date logDate = this.queryForObject("getLastPersonChangeLogDate", Date.class, session);
			// if there is no log entry return the current date time as last log date
			if (!present(logDate)) {
				logDate = new Date();
			}
			searchIndexSyncState.setLast_log_date(logDate);
			return searchIndexSyncState;
		}
	}
}
