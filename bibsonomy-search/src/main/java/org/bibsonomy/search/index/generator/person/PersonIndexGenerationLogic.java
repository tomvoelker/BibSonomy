package org.bibsonomy.search.index.generator.person;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.database.managers.GeneralDatabaseManager;
import org.bibsonomy.model.Person;
import org.bibsonomy.database.common.AbstractDatabaseManagerWithSessionManagement;
import org.bibsonomy.search.index.generator.IndexGenerationLogic;
import org.bibsonomy.search.management.database.params.SearchParam;
import org.bibsonomy.search.update.SearchIndexSyncState;

import java.util.List;

/**
 * logic to retrieve all person data from the database
 *
 * @author dzo
 */
public class PersonIndexGenerationLogic extends AbstractDatabaseManagerWithSessionManagement implements IndexGenerationLogic<Person> {

	@Override
	public int getNumberOfEntities() {
		try (final DBSession session = this.openSession()) {
			return this.queryForObject("getPersonsCount", Integer.class, session);
		}
	}

	@Override
	public SearchIndexSyncState getDbState() {
		try (final DBSession session = this.openSession()) {
			final SearchIndexSyncState searchIndexSyncState = new SearchIndexSyncState();
			final Integer lastId = this.queryForObject("getLastPersonChangeId", Integer.class, session);
			searchIndexSyncState.setLastPersonChangeId(lastId);
			return searchIndexSyncState;
		}
	}

	@Override
	public List<Person> getEntites(int lastPersonId, int limit) {
		try (final DBSession session = this.openSession()) {
			final SearchParam param = new SearchParam();
			param.setLastContentId(lastPersonId);
			param.setLimit(limit);
			return this.queryForList("getPersons", param, Person.class, session);
		}
	}
}
