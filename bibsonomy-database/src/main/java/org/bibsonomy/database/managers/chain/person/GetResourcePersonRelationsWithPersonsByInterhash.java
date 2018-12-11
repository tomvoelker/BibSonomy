package org.bibsonomy.database.managers.chain.person;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.PersonDatabaseManager;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.logic.querybuilder.ResourcePersonRelationQueryBuilder;

import java.util.List;

import static org.bibsonomy.util.ValidationUtils.present;


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
    protected List<ResourcePersonRelation> handle(ResourcePersonRelationQueryBuilder param, DBSession session) {
        return this.getPersonDatabaseManager().getResourcePersonRelationsWithPersonsByInterhash(param.getInterhash(), session);
    }

    @Override
    protected boolean canHandle(ResourcePersonRelationQueryBuilder param) {
        return present(param.getInterhash()) &&
               !param.isWithPosts() &&
               !present(param.getAuthorIndex()) &&
               !present(param.getPersonId()) &&
               !present(param.getRelationType());
    }
}
