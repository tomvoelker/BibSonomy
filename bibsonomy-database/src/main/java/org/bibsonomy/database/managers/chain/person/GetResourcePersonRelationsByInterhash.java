package org.bibsonomy.database.managers.chain.person;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.PersonDatabaseManager;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.logic.querybuilder.ResourcePersonRelationQueryBuilder;

import java.util.List;

import static org.bibsonomy.util.ValidationUtils.present;

/**
 * Handles cases, where an interhash is set.
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
    protected List<ResourcePersonRelation> handle(ResourcePersonRelationQueryBuilder param, DBSession session) {
        return this.getPersonDatabaseManager().getResourcePersonRelations(param.getInterhash(), param.getAuthorIndex(), param.getRelationType(), session);
    }

    @Override
    protected boolean canHandle(ResourcePersonRelationQueryBuilder param) {
        return present(param.getInterhash()) &&
               present(param.getAuthorIndex()) &&
               present(param.getRelationType()) &&
               !param.isWithPosts() &&
               !param.isWithPersons() &&
               !present(param.getPersonId());
    }
}
