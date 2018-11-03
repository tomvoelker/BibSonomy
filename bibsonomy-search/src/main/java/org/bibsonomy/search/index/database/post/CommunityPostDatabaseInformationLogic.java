package org.bibsonomy.search.index.database.post;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.ResourceAwareAbstractDatabaseManagerWithSessionManagement;
import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.model.Resource;
import org.bibsonomy.search.index.database.DatabaseInformationLogic;
import org.bibsonomy.search.update.DefaultSearchIndexSyncState;
import org.bibsonomy.search.update.SearchCommunityIndexSyncState;

import java.util.Date;

/**
 * database information for community posts
 *
 * @author dzo
 */
public class CommunityPostDatabaseInformationLogic<R extends Resource> extends ResourceAwareAbstractDatabaseManagerWithSessionManagement<R> implements DatabaseInformationLogic<SearchCommunityIndexSyncState> {

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
	public SearchCommunityIndexSyncState getDbState() {
		final SearchCommunityIndexSyncState searchCommunityIndexSyncState = new SearchCommunityIndexSyncState();
		searchCommunityIndexSyncState.setNormalSearchIndexState(this.normalPostDatabaseInformationLogic.getDbState());
		searchCommunityIndexSyncState.setCommunitySearchIndexState(this.queryForCommunitySearchIndexState());
		return searchCommunityIndexSyncState;
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
			return searchIndexSyncState;
		}
	}
}
