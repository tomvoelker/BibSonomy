package org.bibsonomy.database.managers.chain.person;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.PersonDatabaseManager;
import org.bibsonomy.database.managers.chain.util.QueryAdapter;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.logic.query.PersonQuery;

/**
 * returns all persons of an organization
 *
 * @author dzo
 */
public class GetPersonsByOrganization extends PersonChainElement {

	/**
	 * default constructor
	 *
	 * @param personDatabaseManager
	 */
	public GetPersonsByOrganization(PersonDatabaseManager personDatabaseManager) {
		super(personDatabaseManager);
	}

	@Override
	protected List<Person> handle(final QueryAdapter<PersonQuery> param, final DBSession session) {
		final PersonQuery query = param.getQuery();
		final Group organization = query.getOrganization();
		final int start = query.getStart();
		final int end = query.getEnd();
		return this.getPersonDatabaseManager().getPersonsByOrganization(organization, end - start, start, session);
	}

	@Override
	protected boolean canHandle(final QueryAdapter<PersonQuery> param) {
		return present(param.getQuery().getOrganization());
	}
}
