package org.bibsonomy.search.index.database.cris;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.database.common.AbstractDatabaseManagerWithSessionManagement;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.search.index.database.DatabaseInformationLogic;
import org.bibsonomy.search.update.DefaultSearchIndexSyncState;

import java.util.Date;

/**
 * information logic for {@link org.bibsonomy.model.cris.CRISLink} indices
 *
 * @author dzo
 */
public class CRISLinkDatabaseInformationLogic extends AbstractDatabaseManagerWithSessionManagement implements DatabaseInformationLogic<DefaultSearchIndexSyncState> {

	@Override
	public DefaultSearchIndexSyncState getDbState() {
		try (final DBSession session = this.openSession()) {
			final DefaultSearchIndexSyncState searchIndexSyncState = new DefaultSearchIndexSyncState();
			final Integer lastId = this.queryForObject("getLastCRISLinkId", ConstantID.LINKABLE_ID.getId(), Integer.class, session);
			searchIndexSyncState.setLastPostContentId(lastId);
			Date logDate = this.queryForObject("getLastCRISLinkLogDate", Date.class, session);
			// if there is no log entry return the current date time as last log date
			if (!present(logDate)) {
				logDate = new Date();
			}
			searchIndexSyncState.setLast_log_date(logDate);
			return searchIndexSyncState;
		}
	}
}
