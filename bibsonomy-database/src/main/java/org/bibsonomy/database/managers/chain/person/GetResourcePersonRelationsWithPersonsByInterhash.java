package org.bibsonomy.database.managers.chain.person;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.PersonDatabaseManager;
import org.bibsonomy.database.managers.chain.util.QueryAdapter;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.logic.query.ResourcePersonRelationQuery;
import org.bibsonomy.model.logic.querybuilder.ResourcePersonRelationQueryBuilder;

import java.util.List;

import static org.bibsonomy.util.ValidationUtils.present;

/**
 * @author ada
 */
public class GetResourcePersonRelationsWithPersonsByInterhash extends ResourcePersonRelationChainElement {

	/**
	 * Creates an instance with the person database manager set.
	 *
	 * @param personDatabaseManager an instance.
	 */
	public GetResourcePersonRelationsWithPersonsByInterhash(PersonDatabaseManager personDatabaseManager) {
		super(personDatabaseManager);
	}

	@Override
	protected List<ResourcePersonRelation> handle(QueryAdapter<ResourcePersonRelationQuery> adapter, DBSession session) {
		return this.getPersonDatabaseManager().getResourcePersonRelationsWithPersonsByInterhash(adapter.getQuery().getInterhash(), session);
	}

	@Override
	protected boolean canHandle(QueryAdapter<ResourcePersonRelationQuery> adapter) {
		final ResourcePersonRelationQuery query = adapter.getQuery();
		return present(query.getInterhash()) &&
						!query.isWithPosts() &&
						!present(query.getAuthorIndex()) &&
						!present(query.getPersonId()) &&
						!present(query.getRelationType());
	}
}
