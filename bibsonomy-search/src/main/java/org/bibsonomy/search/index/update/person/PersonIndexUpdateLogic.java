package org.bibsonomy.search.index.update.person;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.model.Person;
import org.bibsonomy.search.index.database.person.PersonDatabaseInformationLogic;
import org.bibsonomy.search.index.update.IndexUpdateLogic;
import org.bibsonomy.search.management.database.params.SearchParam;
import org.bibsonomy.search.update.SearchIndexSyncState;

import java.util.List;

/**
 * the indexUpdateLogic implementation for {@link Person}s
 *
 * @author dzo
 */
public class PersonIndexUpdateLogic extends PersonDatabaseInformationLogic implements IndexUpdateLogic<Person> {

	@Override
	public List<Person> getNewerEntities(long lastEntityId, int size, int offset) {
		try (final DBSession session = this.openSession()) {
			final SearchParam param = new SearchParam();
			param.setLastContentId(lastEntityId);
			param.setLimit(size);
			param.setOffset(offset);
			return this.queryForList("getUpdatedAndNewPersons", param, Person.class, session);
		}
	}
}
