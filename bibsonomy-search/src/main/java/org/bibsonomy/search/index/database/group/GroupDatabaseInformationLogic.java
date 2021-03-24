package org.bibsonomy.search.index.database.group;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Date;

import org.bibsonomy.database.common.AbstractDatabaseManagerWithSessionManagement;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.search.index.database.DatabaseInformationLogic;
import org.bibsonomy.search.update.DefaultSearchIndexSyncState;

/**
 * TODO: merge with {@link org.bibsonomy.search.index.database.cris.CRISLinkDatabaseInformationLogic}
 *
 * information logic for group index
 *
 * @author dzo
 */
public class GroupDatabaseInformationLogic extends AbstractDatabaseManagerWithSessionManagement implements DatabaseInformationLogic<DefaultSearchIndexSyncState> {

	@Override
	public DefaultSearchIndexSyncState getDbState() {
		try (final DBSession session = this.openSession()) {
			final DefaultSearchIndexSyncState searchIndexSyncState = new DefaultSearchIndexSyncState();
			final Integer lastId = this.queryForObject("getLastGroupId", ConstantID.GROUP_ID.getId(), Integer.class, session);
			searchIndexSyncState.setLastPostContentId(lastId);
			Date logDate = this.queryForObject("getLastGroupLogDate", Date.class, session);
			// if there is no log entry return the current date time as last log date
			if (!present(logDate)) {
				logDate = new Date();
			}
			searchIndexSyncState.setLast_log_date(logDate);
			return searchIndexSyncState;
		}
	}
}
