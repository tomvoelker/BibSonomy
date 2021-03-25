package org.bibsonomy.database.managers.chain.personresourceRelation;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.PersonDatabaseManager;
import org.bibsonomy.database.managers.chain.util.QueryAdapter;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.logic.query.ResourcePersonRelationQuery;

import java.util.List;

import static org.bibsonomy.util.ValidationUtils.present;

/**
 * Handles cases, where an interhash is set.
 *
 * @author ada
 */
public class GetResourcePersonRelationsByInterhash extends ResourcePersonRelationChainElement {

	/**
	 * Creates an instance with the person database manager set.
	 *
	 * @param personDatabaseManager an instance.
	 */
	public GetResourcePersonRelationsByInterhash(PersonDatabaseManager personDatabaseManager) {
		super(personDatabaseManager);
	}

	@Override
	protected List<ResourcePersonRelation> handle(QueryAdapter<ResourcePersonRelationQuery> adapter, DBSession session) {
		return this.getPersonDatabaseManager().getResourcePersonRelations(adapter.getQuery().getInterhash(), adapter.getQuery().getAuthorIndex(), adapter.getQuery().getRelationType(), session);
	}

	@Override
	protected boolean canHandle(QueryAdapter<ResourcePersonRelationQuery> adapter) {
		final ResourcePersonRelationQuery query = adapter.getQuery();
		return present(query.getInterhash()) &&
						present(query.getAuthorIndex()) &&
						present(query.getRelationType()) &&
						!query.isWithPosts() &&
						!query.isWithPersons() &&
						!present(query.getPersonId());
	}
}
