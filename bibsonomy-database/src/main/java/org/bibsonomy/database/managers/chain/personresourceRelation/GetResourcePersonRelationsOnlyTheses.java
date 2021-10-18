package org.bibsonomy.database.managers.chain.personresourceRelation;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.PersonDatabaseManager;
import org.bibsonomy.database.managers.chain.util.QueryAdapter;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.logic.query.ResourcePersonRelationQuery;

/**
 * Chain element to retrieve person resource relations of a person, that are
 * with an entrytype containing 'thesis'.
 *
 * @author kchoong
 */
public class GetResourcePersonRelationsOnlyTheses extends ResourcePersonRelationChainElement {

    /**
     * Creates an instance with the person database manager set.
     *
     * @param personDatabaseManager an instance.
     */
    public GetResourcePersonRelationsOnlyTheses(PersonDatabaseManager personDatabaseManager) {
        super(personDatabaseManager);
    }

    @Override
    protected List<ResourcePersonRelation> handle(QueryAdapter<ResourcePersonRelationQuery> param, DBSession session) {
        return this.getPersonDatabaseManager().getResourcePersonRelationsOnlyTheses(param.getQuery().getPersonId(), session);
    }

    @Override
    protected boolean canHandle(QueryAdapter<ResourcePersonRelationQuery> param) {
        final ResourcePersonRelationQuery query = param.getQuery();
        return present(query.getPersonId()) &&
                query.isOnlyTheses() &&
                !query.isExcludeTheses() &&
                query.isWithPosts() &&
                !present(query.getAuthorIndex()) &&
                !present(query.getRelationType());
    }
}
