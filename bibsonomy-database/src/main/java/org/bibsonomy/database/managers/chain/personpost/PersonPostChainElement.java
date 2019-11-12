package org.bibsonomy.database.managers.chain.personpost;

import org.bibsonomy.database.managers.PersonDatabaseManager;
import org.bibsonomy.database.managers.chain.ChainElement;
import org.bibsonomy.database.managers.chain.util.QueryAdapter;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.logic.query.PersonPostQuery;

import java.util.List;

/**
 * An abstract element of the chain handling PersonPosts queries.
 * @author kchoong
 */
public abstract class PersonPostChainElement extends ChainElement<List<Post>, QueryAdapter<PersonPostQuery>> {

    private final PersonDatabaseManager personDatabaseManager;

    /**
     * Creates an instance with the person database manager set.
     *
     * @param personDatabaseManager an instance.
     */
    public PersonPostChainElement(PersonDatabaseManager personDatabaseManager) {
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
