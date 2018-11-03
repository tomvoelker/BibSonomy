package org.bibsonomy.search.index.database.post;

import org.bibsonomy.database.common.AbstractDatabaseManagerWithSessionManagement;
import org.bibsonomy.search.index.database.DatabaseInformationLogic;
import org.bibsonomy.search.update.DefaultSearchIndexSyncState;

/**
 * database informations for community posts
 *
 * @author dzo
 */
public class CommunityPostDatabaseInformationLogic extends AbstractDatabaseManagerWithSessionManagement implements DatabaseInformationLogic {

	@Override
	public DefaultSearchIndexSyncState getDbState() {
		return null;
	}
}
