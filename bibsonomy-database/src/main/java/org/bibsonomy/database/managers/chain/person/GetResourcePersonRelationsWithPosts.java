package org.bibsonomy.database.managers.chain.person;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.PersonDatabaseManager;
import org.bibsonomy.database.managers.chain.util.QueryAdapter;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.logic.query.ResourcePersonRelationQuery;
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
    protected List<ResourcePersonRelation> handle(QueryAdapter<ResourcePersonRelationQuery> adapter, DBSession session) {

        final ResourcePersonRelationQuery query = adapter.getQuery();

        final List<ResourcePersonRelation> relations = this.getPersonDatabaseManager().getResourcePersonRelationsWithPosts(
                query.getPersonId(), adapter.getLoggedinUser(), GoldStandardPublication.class, session);

        //FIXME use a join to retrieve the necessary information
        if (query.isWithPersonsOfPosts()) {
            for (final ResourcePersonRelation resourcePersonRelation : relations) {
                final String interHash = resourcePersonRelation.getPost().getResource().getInterHash();
                final ResourcePersonRelationQueryBuilder relsBuilder = new ResourcePersonRelationQueryBuilder()
                        .byInterhash(interHash)
                        .withPersons(true);

                final List<ResourcePersonRelation> relsOfPub = this.getPersonDatabaseManager()
                        .getResourcePersonRelationsWithPersonsByInterhash(relsBuilder.getInterhash(), session);

                resourcePersonRelation.getPost().setResourcePersonRelations(relsOfPub);
            }
        }

        return relations;
    }

    @Override
    protected boolean canHandle(QueryAdapter<ResourcePersonRelationQuery> adapter) {
        return present(adapter.getQuery().getPersonId()) &&
                !adapter.getQuery().isWithPersons() &&
                !present(adapter.getQuery().getAuthorIndex()) &&
                !present(adapter.getQuery().getRelationType());
    }
}
