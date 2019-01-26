package org.bibsonomy.database.managers.chain.statistic.person;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.PersonDatabaseManager;
import org.bibsonomy.database.managers.chain.util.QueryAdapter;
import org.bibsonomy.model.logic.query.PersonQuery;
import org.bibsonomy.model.statistics.Statistics;

/**
 * gets the count by the person search
 *
 * @author dzo
 */
public class GetPersonsByFulltextSearchCount extends PersonStatisticsChainElement {

	/**
	 * default constructor
	 *
	 * @param personDatabaseManager
	 */
	public GetPersonsByFulltextSearchCount(PersonDatabaseManager personDatabaseManager) {
		super(personDatabaseManager);
	}

	@Override
	protected Statistics handle(QueryAdapter<PersonQuery> param, DBSession session) {
		return this.personDatabaseManager.getPersonsByFulltextSearchCount(param.getLoggedinUser(), param.getQuery());
	}

	@Override
	protected boolean canHandle(QueryAdapter<PersonQuery> param) {
		return true;
	}
}
