package org.bibsonomy.search.index.generator.person;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.search.index.database.person.PersonDatabaseInformationLogic;
import org.bibsonomy.search.index.generator.OneToManyIndexGenerationLogic;
import org.bibsonomy.search.management.database.params.SearchParam;

import java.util.List;

/**
 * logic to retrieve all person data from the database
 *
 * @author dzo
 */
public class PersonIndexGenerationLogic extends PersonDatabaseInformationLogic implements OneToManyIndexGenerationLogic<Person, ResourcePersonRelation> {

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
