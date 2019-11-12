package org.bibsonomy.database.managers.chain.personpost.get;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.PersonDatabaseManager;
import org.bibsonomy.database.managers.chain.personpost.PersonPostChainElement;
import org.bibsonomy.database.managers.chain.util.QueryAdapter;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.logic.query.PersonPostQuery;

import java.util.List;

import static org.bibsonomy.util.ValidationUtils.present;

public class GetPersonPosts extends PersonPostChainElement {
    /**
     * Creates an instance with the person database manager set.
     *
     * @param personDatabaseManager an instance.
     */
    public GetPersonPosts(PersonDatabaseManager personDatabaseManager) {
        super(personDatabaseManager);
    }

    @Override
    protected List<Post> handle(QueryAdapter<PersonPostQuery> adapter, DBSession session) {
        final PersonPostQuery query = adapter.getQuery();

        final int offset = query.getStart();
        final int limit = query.getEnd() - offset;
        final List<Post> personPosts = this.getPersonDatabaseManager().getPersonPosts(query.getPersonId(), limit, offset, session);

        return personPosts;
    }

    @Override
    protected boolean canHandle(QueryAdapter<PersonPostQuery> adapter) {
        final PersonPostQuery query = adapter.getQuery();

        return present(query.getPersonId());
    }
}
