package org.bibsonomy.database.managers.chain.util;

import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.query.Query;

/**
 * adapter to pass a query with information through a chain
 * @param <T>
 *
 * @author dzo
 */
public class QueryAdapter<T extends Query> {

    private final T query;

    private final User loggedinUser;

    /**
     * default constructor
     * @param query the query
     * @param loggedinUser the logged in user
     */
    public QueryAdapter(T query, User loggedinUser) {
        this.query = query;
        this.loggedinUser = loggedinUser;
    }

    /**
     * @return the query
     */
    public T getQuery() {
        return query;
    }

    /**
     * @return the loggedinUser
     */
    public User getLoggedinUser() {
        return loggedinUser;
    }
}
