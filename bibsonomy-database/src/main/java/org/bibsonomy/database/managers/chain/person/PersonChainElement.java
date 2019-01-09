package org.bibsonomy.database.managers.chain.person;

import java.util.List;

import org.bibsonomy.database.managers.PersonDatabaseManager;
import org.bibsonomy.database.managers.chain.ChainElement;
import org.bibsonomy.database.managers.chain.util.QueryAdapter;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.logic.query.PersonQuery;

/**
 * @author dzo
 */
public abstract class PersonChainElement extends ChainElement<List<Person>, QueryAdapter<PersonQuery>> {
	private final PersonDatabaseManager personDatabaseManager;

	/**
	 * default constructor
	 * @param personDatabaseManager
	 */
	public PersonChainElement(PersonDatabaseManager personDatabaseManager) {
		this.personDatabaseManager = personDatabaseManager;
	}

	/**
	 * @return the personDatabaseManager
	 */
	public PersonDatabaseManager getPersonDatabaseManager() {
		return personDatabaseManager;
	}
}
