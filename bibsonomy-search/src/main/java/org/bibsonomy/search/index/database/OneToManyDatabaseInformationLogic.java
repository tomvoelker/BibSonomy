package org.bibsonomy.search.index.database;

import org.bibsonomy.search.update.DefaultSearchIndexSyncState;
import org.bibsonomy.search.update.SearchIndexDualSyncState;

/**
 * adapter to build a one to many database information logic of two {@link DatabaseInformationLogic}s
 *
 * @author dzo
 */
public class OneToManyDatabaseInformationLogic implements DatabaseInformationLogic<SearchIndexDualSyncState> {

	private final DatabaseInformationLogic<DefaultSearchIndexSyncState> firstDatabaseInformationLogic;
	private final DatabaseInformationLogic<DefaultSearchIndexSyncState> secondDatabaseInformationLogic;

	/**
	 * constructor with the required two information services
	 * @param firstDatabaseInformationLogic
	 * @param secondDatabaseInformationLogic
	 */
	public OneToManyDatabaseInformationLogic(DatabaseInformationLogic<DefaultSearchIndexSyncState> firstDatabaseInformationLogic, DatabaseInformationLogic<DefaultSearchIndexSyncState> secondDatabaseInformationLogic) {
		this.firstDatabaseInformationLogic = firstDatabaseInformationLogic;
		this.secondDatabaseInformationLogic = secondDatabaseInformationLogic;
	}

	@Override
	public SearchIndexDualSyncState getDbState() {
		final SearchIndexDualSyncState searchIndexDualSyncState = new SearchIndexDualSyncState();

		// get first state
		final DefaultSearchIndexSyncState firstState = this.firstDatabaseInformationLogic.getDbState();
		searchIndexDualSyncState.setFirstState(firstState);

		// get second state
		final DefaultSearchIndexSyncState secondState = this.secondDatabaseInformationLogic.getDbState();
		searchIndexDualSyncState.setSecondState(secondState);

		return searchIndexDualSyncState;
	}
}
