package org.bibsonomy.database.managers.chain.personresourceRelation;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.PersonDatabaseManager;
import org.bibsonomy.database.managers.chain.util.QueryAdapter;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.logic.query.ResourcePersonRelationQuery;

import java.util.List;

import static org.bibsonomy.util.ValidationUtils.present;

/**
 * Handles cases, where an interhash, an index and a type is set.
 *
 * @author ada
 */
public class GetResourcePersonRelationsByInterhashIndexAndType extends ResourcePersonRelationChainElement {

	/**
	 * Creates an instance with the person database manager set.
	 *
	 * @param personDatabaseManager an instance.
	 */
	public GetResourcePersonRelationsByInterhashIndexAndType(final PersonDatabaseManager personDatabaseManager) {
		super(personDatabaseManager);
	}

	@Override
	protected List<ResourcePersonRelation> handle(final QueryAdapter<ResourcePersonRelationQuery> adapter, final DBSession session) {
		final ResourcePersonRelationQuery query = adapter.getQuery();
		return this.getPersonDatabaseManager().getResourcePersonRelations(query.getInterhash(), query.getAuthorIndex(), query.getRelationType(), session);
	}

	@Override
	protected boolean canHandle(final QueryAdapter<ResourcePersonRelationQuery> adapter) {
		final ResourcePersonRelationQuery query = adapter.getQuery();
		return present(query.getInterhash()) &&
						present(query.getAuthorIndex()) &&
						present(query.getRelationType()) &&
						!query.isWithPosts() &&
						!query.isWithPersons() &&
						!present(query.getPersonId());
	}
}
