package org.bibsonomy.database.managers.chain.person;

import java.util.List;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.PersonDatabaseManager;
import org.bibsonomy.database.managers.chain.util.QueryAdapter;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.logic.query.PersonQuery;

/**
 * retrieve persons from the fulltext search
 *
 * @author dzo
 */
public class GetPersonsByFulltextSearch extends PersonChainElement {

	/**
	 * default constructor
	 *
	 * @param personDatabaseManager
	 */
	public GetPersonsByFulltextSearch(PersonDatabaseManager personDatabaseManager) {
		super(personDatabaseManager);
	}

	@Override
	protected List<Person> handle(QueryAdapter<PersonQuery> param, DBSession session) {
		return this.getPersonDatabaseManager().getPersonsByFulltextSearch(param.getQuery(), param.getLoggedinUser());
	}

	@Override
	protected boolean canHandle(QueryAdapter<PersonQuery> param) {
		return true;
	}
}
