package org.bibsonomy.database.managers.chain.personresourceRelation;

import java.util.List;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.PersonDatabaseManager;
import org.bibsonomy.database.managers.chain.util.QueryAdapter;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.logic.query.ResourcePersonRelationQuery;

/**
 * Chain element to retrieve person resource relations of a person,
 * excluding all entrytypes containing 'thesis'.
 *
 * @author kchoong
 */
public class GetResourcePersonRelationsExcludeTheses extends ResourcePersonRelationChainElement {

    /**
     * Creates an instance with the person database manager set.
     *
     * @param personDatabaseManager an instance.
     */
    public GetResourcePersonRelationsExcludeTheses(PersonDatabaseManager personDatabaseManager) {
        super(personDatabaseManager);
    }

    @Override
    protected List<ResourcePersonRelation> handle(QueryAdapter<ResourcePersonRelationQuery> param, DBSession session) {
        return null;
    }

    @Override
    protected boolean canHandle(QueryAdapter<ResourcePersonRelationQuery> param) {
        return false;
    }
}
