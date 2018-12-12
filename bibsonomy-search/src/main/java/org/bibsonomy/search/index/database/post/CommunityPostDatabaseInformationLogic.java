package org.bibsonomy.search.index.database.post;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.ResourceAwareAbstractDatabaseManagerWithSessionManagement;
import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.model.Resource;
import org.bibsonomy.search.index.database.DatabaseInformationLogic;
import org.bibsonomy.search.update.DefaultSearchIndexSyncState;
import org.bibsonomy.search.update.SearchIndexDualSyncState;

import java.util.Date;

/**
 * database information for community posts
 *
 * @author dzo
 */
public class CommunityPostDatabaseInformationLogic<R extends Resource> extends ResourceAwareAbstractDatabaseManagerWithSessionManagement<R> implements DatabaseInformationLogic<SearchIndexDualSyncState> {

	private final DatabaseInformationLogic<DefaultSearchIndexSyncState> normalPostDatabaseInformationLogic;

	/**
	 * default constructor
	 *
	 * @param resourceClass
	 * @param normalPostDatabaseInformationLogic
	 */
	public CommunityPostDatabaseInformationLogic(Class<R> resourceClass, DatabaseInformationLogic<DefaultSearchIndexSyncState> normalPostDatabaseInformationLogic) {
		super(resourceClass);
		this.normalPostDatabaseInformationLogic = normalPostDatabaseInformationLogic;
	}

	@Override
	public SearchIndexDualSyncState getDbState() {
		final SearchIndexDualSyncState searchIndexDualSyncState = new SearchIndexDualSyncState();
		searchIndexDualSyncState.setSecondState(this.normalPostDatabaseInformationLogic.getDbState());
		searchIndexDualSyncState.setFirstState(this.queryForCommunitySearchIndexState());
		return searchIndexDualSyncState;
	}

	private DefaultSearchIndexSyncState queryForCommunitySearchIndexState() {
		try (final DBSession session = this.openSession()) {
			final DefaultSearchIndexSyncState searchIndexSyncState = new DefaultSearchIndexSyncState();
			final ConstantID contentType = this.getConstantID();
			final int contentTypeId = contentType.getId();

			final Date lastLogDate = this.queryForObject("getLastLogDateCommunity", contentTypeId, Date.class, session);
			searchIndexSyncState.setLast_log_date(lastLogDate);

			final Integer lastContentId = this.queryForObject("getLastContentIdCommunity", contentTypeId, Integer.class, session);
			searchIndexSyncState.setLast_tas_id(lastContentId);

			final Integer lastPersonChangeId = this.queryForObject("getLastPersonChangeId", Integer.class, session);
			searchIndexSyncState.setLastPersonChangeId(lastPersonChangeId);

			Date lastPersonLogDate = this.queryForObject("getLastPersonChangeLogDate", Date.class, session);
			if (!present(lastPersonLogDate)) {
				lastPersonLogDate = new Date();
			}
			searchIndexSyncState.setLastPersonLogDate(lastPersonLogDate);
			return searchIndexSyncState;
		}
	}
}
