package org.bibsonomy.database.managers.chain.statistic.person;

import org.bibsonomy.database.managers.PersonDatabaseManager;
import org.bibsonomy.database.managers.chain.ChainElement;
import org.bibsonomy.model.logic.query.ProjectQuery;
import org.bibsonomy.model.statistics.Statistics;

/**
 * abstract statistic chain element for person
 * @author dzo
 */
public abstract class PersonStatisticsChainElement extends ChainElement<Statistics, ProjectQuery> {

	protected PersonDatabaseManager personDatabaseManager;

	/**
	 * @param personDatabaseManager the projectDatabaseManager to set
	 */
	public void setProjectDatabaseManager(PersonDatabaseManager personDatabaseManager) {
		this.personDatabaseManager = personDatabaseManager;
	}
}
