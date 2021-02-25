package org.bibsonomy.search.index.database.project;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.database.common.AbstractDatabaseManagerWithSessionManagement;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.search.index.database.DatabaseInformationLogic;
import org.bibsonomy.search.update.DefaultSearchIndexSyncState;

import java.util.Date;

/**
 * the database information logic for {@link org.bibsonomy.model.cris.Project}s
 *
 * @author dzo
 */
public class ProjectDatabaseInformationLogic extends AbstractDatabaseManagerWithSessionManagement implements DatabaseInformationLogic<DefaultSearchIndexSyncState> {

	@Override
	public DefaultSearchIndexSyncState getDbState() {
		try (final DBSession session = this.openSession()) {
			final DefaultSearchIndexSyncState searchIndexSyncState = new DefaultSearchIndexSyncState();
			final Integer lastId = this.queryForObject("getLastProjectChangeId", ConstantID.PROJECT_ID.getId(), Integer.class, session);
			searchIndexSyncState.setLastPostContentId(lastId);
			Date logDate = this.queryForObject("getLastProjectChangeLogDate", Date.class, session);
			// if there is no log entry return the current date time as last log date
			if (!present(logDate)) {
				logDate = new Date();
			}
			searchIndexSyncState.setLast_log_date(logDate);
			return searchIndexSyncState;
		}
	}
}
