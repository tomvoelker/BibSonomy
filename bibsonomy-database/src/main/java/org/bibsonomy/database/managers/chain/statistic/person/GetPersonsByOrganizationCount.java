package org.bibsonomy.database.managers.chain.statistic.person;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.PersonDatabaseManager;
import org.bibsonomy.database.managers.chain.util.QueryAdapter;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.logic.query.PersonQuery;
import org.bibsonomy.model.statistics.Statistics;

/**
 * @author dzo
 */
public class GetPersonsByOrganizationCount extends PersonStatisticsChainElement {

	/**
	 * default constructor
	 *
	 * @param personDatabaseManager
	 */
	public GetPersonsByOrganizationCount(final PersonDatabaseManager personDatabaseManager) {
		super(personDatabaseManager);
	}

	@Override
	protected Statistics handle(final QueryAdapter<PersonQuery> param, final DBSession session) {
		final Group organization = param.getQuery().getOrganization();
		return this.personDatabaseManager.getPersonsByOrganizationCount(organization, session);
	}

	@Override
	protected boolean canHandle(final QueryAdapter<PersonQuery> param) {
		return present(param.getQuery().getOrganization());
	}
}
