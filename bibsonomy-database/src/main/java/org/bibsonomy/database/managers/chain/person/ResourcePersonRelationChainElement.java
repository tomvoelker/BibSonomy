package org.bibsonomy.database.managers.chain.person;

import org.bibsonomy.database.managers.PersonDatabaseManager;
import org.bibsonomy.database.managers.chain.ChainElement;
import org.bibsonomy.database.managers.chain.util.QueryAdapter;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.logic.query.ResourcePersonRelationQuery;
import org.bibsonomy.model.logic.querybuilder.ResourcePersonRelationQueryBuilder;

import java.util.List;

/**
 * An abstract element of the chain handling ResourcePersonRelation queries.
 */
public abstract class ResourcePersonRelationChainElement extends ChainElement<List<ResourcePersonRelation>, QueryAdapter<ResourcePersonRelationQuery>> {

    private final PersonDatabaseManager personDatabaseManager;

    /**
     * Creates an instance with the person database manager set.
     *
     * @param personDatabaseManager an instance.
     */
    public ResourcePersonRelationChainElement(PersonDatabaseManager personDatabaseManager) {
        this.personDatabaseManager = personDatabaseManager;
    }

    /**
     * Gets the person database manager.
     *
     * @return the person database manger instance.
     */
    public PersonDatabaseManager getPersonDatabaseManager() {
        return personDatabaseManager;
    }
}
