package org.bibsonomy.database.managers.chain.statistic.person;

import org.bibsonomy.database.managers.PersonDatabaseManager;
import org.bibsonomy.database.managers.chain.ChainElement;
import org.bibsonomy.database.managers.chain.util.QueryAdapter;
import org.bibsonomy.model.logic.query.PersonQuery;
import org.bibsonomy.model.statistics.Statistics;

/**
 * abstract statistic chain element for person
 *
 * @author dzo
 */
public abstract class PersonStatisticsChainElement extends ChainElement<Statistics, QueryAdapter<PersonQuery>> {

	protected final PersonDatabaseManager personDatabaseManager;

	/**
	 * default constructor
	 *
	 * @param personDatabaseManager
	 */
	public PersonStatisticsChainElement(PersonDatabaseManager personDatabaseManager) {
		this.personDatabaseManager = personDatabaseManager;
	}
}
