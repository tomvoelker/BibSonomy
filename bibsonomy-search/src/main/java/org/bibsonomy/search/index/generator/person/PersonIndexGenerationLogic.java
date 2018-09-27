package org.bibsonomy.search.index.generator.person;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.database.managers.GeneralDatabaseManager;
import org.bibsonomy.model.Person;
import org.bibsonomy.database.common.AbstractDatabaseManagerWithSessionManagement;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.search.index.generator.IndexGenerationLogic;
import org.bibsonomy.search.index.generator.OneToManyIndexGenerationLogic;
import org.bibsonomy.search.management.database.params.SearchParam;
import org.bibsonomy.search.update.SearchIndexSyncState;

import java.util.List;

/**
 * logic to retrieve all person data from the database
 *
 * @author dzo
 */
public class PersonIndexGenerationLogic extends AbstractDatabaseManagerWithSessionManagement implements OneToManyIndexGenerationLogic<Person, ResourcePersonRelation> {

	private static SearchParam buildParam(int lastPersonId, int limit) {
		final SearchParam param = new SearchParam();
		param.setLastContentId(lastPersonId);
		param.setLimit(limit);
		return param;
	}

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
			final SearchParam param = buildParam(lastPersonId, limit);
			return this.queryForList("getPersons", param, Person.class, session);
		}
	}

	@Override
	public List<ResourcePersonRelation> getToManyEntities(int lastContentId, int limit) {
		try (final DBSession session = this.openSession()) {
			final SearchParam param = buildParam(lastContentId, limit);
			return this.queryForList("getResourceRelations", param, ResourcePersonRelation.class, session);
		}
	}

	@Override
	public int getNumberOfToManyEntities() {
		try (final DBSession session = this.openSession()) {
			return this.queryForObject("getPersonRelationsCount", Integer.class, session);
		}
	}
}
