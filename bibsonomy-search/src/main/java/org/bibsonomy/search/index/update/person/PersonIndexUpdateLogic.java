package org.bibsonomy.search.index.update.person;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.model.Person;
import org.bibsonomy.search.index.database.person.PersonDatabaseInformationLogic;
import org.bibsonomy.search.index.update.IndexUpdateLogic;
import org.bibsonomy.search.index.utils.SearchParamUtils;
import org.bibsonomy.search.management.database.params.SearchParam;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * the indexUpdateLogic implementation for {@link Person}s
 *
 * @author dzo
 */
public class PersonIndexUpdateLogic extends PersonDatabaseInformationLogic implements IndexUpdateLogic<Person> {

	@Override
	public List<Person> getNewerEntities(long lastEntityId, Date lastLogDate, int size, int offset) {
		try (final DBSession session = this.openSession()) {
			final SearchParam param = SearchParamUtils.buildSeachParam(lastEntityId, lastLogDate, size, offset);
			return this.queryForList("getUpdatedAndNewPersons", param, Person.class, session);
		}
	}

	@Override
	public List<Person> getDeletedEntities(Date lastLogDate) {
		// XXX: currently persons can not be deleted
		return Collections.emptyList();
	}

}
