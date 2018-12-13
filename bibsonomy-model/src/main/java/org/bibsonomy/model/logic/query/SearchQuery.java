package org.bibsonomy.model.logic.query;

/**
 * Makes a query searchable.
 */
public interface SearchQuery {

    /**
     * Returns the search string.
     *
     * @return the search string.
     */
    String getSearch();


    /**
     * Signals that this query has a search string.
     *
     * @return <code>true</code> if the query contains a search string, <code>false</code> otherwise.
     */
    default boolean hasSearch() {
        return true;
    }
}
