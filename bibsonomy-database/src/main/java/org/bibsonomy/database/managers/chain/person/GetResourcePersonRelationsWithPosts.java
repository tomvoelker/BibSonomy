package org.bibsonomy.database.managers.chain.person;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.PersonDatabaseManager;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.logic.querybuilder.ResourcePersonRelationQueryBuilder;

import java.util.List;

import static org.bibsonomy.util.ValidationUtils.present;

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
    protected List<ResourcePersonRelation> handle(ResourcePersonRelationQueryBuilder param, DBSession session) {
        final List<ResourcePersonRelation> rVal = this.getPersonDatabaseManager().getResourcePersonRelationsWithPosts(
                param.getPersonId(), this.loginUser, GoldStandardPublication.class, session); //TODO use Adapter that will magically appear after tonight

        if (param.isWithPersonsOfPosts()) {
            for (final ResourcePersonRelation resourcePersonRelation : rVal) {
                final String interHash = resourcePersonRelation.getPost().getResource().getInterHash();
                final ResourcePersonRelationQueryBuilder relsBuilder = new ResourcePersonRelationQueryBuilder()
                        .byInterhash(interHash)
                        .withPersons(true);

                final List<ResourcePersonRelation> relsOfPub = this.getPersonDatabaseManager().getResourcePersonRelationsWithPersonsByInterhash(relsBuilder.getInterhash(), session);
                resourcePersonRelation.getPost().setResourcePersonRelations(relsOfPub);
            }
        }

        return rVal;
    }

    @Override
    protected boolean canHandle(ResourcePersonRelationQueryBuilder param) {
        return present(param.getPersonId()) &&
                !param.isWithPersons() &&
                !present(param.getAuthorIndex()) &&
                !present(param.getRelationType());
    }
}
