package org.bibsonomy.model.logic.query;


/**
 * Adds pagination to a query.
 */
public interface PaginatedQuery {

    /**
     * The start index of the page retrieved by this query.
     *
     * @return the start index.
     */
    int getStart();


    /**
     * The end index of the page retrieved by this query.
     *
     * @return the end index.
     */
    int getEnd();


    /**
     * Signals whether this query is paginated.
     *
     * @return <code>bool</code> if the query is paginated, <code>false</code> otherwise.
     */
    default boolean isPaginated() {
        return true;
    }
}
