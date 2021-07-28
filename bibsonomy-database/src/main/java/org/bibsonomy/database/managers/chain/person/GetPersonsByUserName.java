package org.bibsonomy.database.managers.chain.person;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.PersonDatabaseManager;
import org.bibsonomy.database.managers.chain.util.QueryAdapter;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.logic.query.PersonQuery;

/**
 * returns a list of a single person if the user has claimed a person
 * an empty list if the user has not claimed a person
 *
 * @author dzo
 */
public class GetPersonsByUserName extends PersonChainElement {

	/**
	 * default constructor
	 *
	 * @param personDatabaseManager
	 */
	public GetPersonsByUserName(final PersonDatabaseManager personDatabaseManager) {
		super(personDatabaseManager);
	}

	@Override
	protected List<Person> handle(final QueryAdapter<PersonQuery> param, final DBSession session) {
		final Person personByUser = this.getPersonDatabaseManager().getPersonByUser(param.getQuery().getUserName(), session);
		if (present(personByUser)) {
			return Arrays.asList(personByUser);
		}
		
		return Collections.emptyList();
	}

	@Override
	protected boolean canHandle(final QueryAdapter<PersonQuery> param) {
		return present(param.getQuery().getUserName());
	}
}
