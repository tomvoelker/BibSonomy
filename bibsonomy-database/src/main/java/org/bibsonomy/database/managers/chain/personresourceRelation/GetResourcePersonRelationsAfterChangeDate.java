package org.bibsonomy.database.managers.chain.personresourceRelation;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Date;
import java.util.List;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.PersonDatabaseManager;
import org.bibsonomy.database.managers.chain.util.QueryAdapter;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.logic.query.ResourcePersonRelationQuery;

public class GetResourcePersonRelationsAfterChangeDate extends ResourcePersonRelationChainElement {

    /**
     * Creates an instance with the person database manager set.
     *
     * @param personDatabaseManager an instance.
     */
    public GetResourcePersonRelationsAfterChangeDate(PersonDatabaseManager personDatabaseManager) {
        super(personDatabaseManager);
    }

    @Override
    protected List<ResourcePersonRelation> handle(QueryAdapter<ResourcePersonRelationQuery> param, DBSession session) {
        final ResourcePersonRelationQuery query = param.getQuery();
        final Date changeDate = query.getAfterChangeDate();

        List<ResourcePersonRelation> relations = this.getPersonDatabaseManager().getResourcePersonRelationsAfterChangeDate(changeDate, session);

        if (query.isWithPersonsOfPosts()) {
            this.getPersonDatabaseManager().loadAllRelationsInPosts(relations, session);
        }

        return relations;
    }

    @Override
    protected boolean canHandle(QueryAdapter<ResourcePersonRelationQuery> param) {
        final ResourcePersonRelationQuery query = param.getQuery();
        return present(query.getAfterChangeDate());
    }
}
