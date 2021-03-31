package org.bibsonomy.database.managers.chain.personresourceRelation;


import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.PersonDatabaseManager;
import org.bibsonomy.database.managers.chain.util.QueryAdapter;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.logic.query.ResourcePersonRelationQuery;
import org.bibsonomy.model.logic.query.util.BasicQueryUtils;

import java.util.List;

/**
 *
 * @author ada
 */
public class GetResourcePersonRelationsWithPosts extends ResourcePersonRelationChainElement {

	/**
	 * Creates an instance with the person database manager set.
	 *
	 * @param personDatabaseManager an instance.
	 */
	public GetResourcePersonRelationsWithPosts(PersonDatabaseManager personDatabaseManager) {
		super(personDatabaseManager);
	}

	@Override
	protected List<ResourcePersonRelation> handle(QueryAdapter<ResourcePersonRelationQuery> adapter, DBSession session) {
		final ResourcePersonRelationQuery query = adapter.getQuery();

		final int offset = BasicQueryUtils.calcOffset(query);
		final int limit = BasicQueryUtils.calcLimit(query);
		final List<ResourcePersonRelation> relations = this.getPersonDatabaseManager().getResourcePersonRelationsWithPosts(query.getPersonId(), limit, offset, session);

		// FIXME use a join to retrieve the necessary information
		if (query.isWithPersonsOfPosts()) {
			for (final ResourcePersonRelation resourcePersonRelation : relations) {
				final String interHash = resourcePersonRelation.getPost().getResource().getInterHash();

				final List<ResourcePersonRelation> relsOfPub = this.getPersonDatabaseManager()
								.getResourcePersonRelationsWithPersonsByInterhash(interHash, session);

				resourcePersonRelation.getPost().setResourcePersonRelations(relsOfPub);
			}
		}

		return relations;
	}

	@Override
	protected boolean canHandle(final QueryAdapter<ResourcePersonRelationQuery> adapter) {
		final ResourcePersonRelationQuery query = adapter.getQuery();
		return present(query.getPersonId()) &&
						!query.isWithPersons() &&
						!present(query.getAuthorIndex()) &&
						!present(query.getRelationType());
	}
}
