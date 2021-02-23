package org.bibsonomy.model.logic.query;


/**
 * A basic query that supports pagination and searching.
 *
 * @author dzo
 */
public class BasicQuery extends BasicPaginatedQuery implements SearchQuery, Query {

    /**
     * free text search
     */
    private String search;

    /**
     * Gets the search string.
     *
     * @return the search string.
     */
    public String getSearch() {
        return search;
    }


    /**
     * Sets the search string.
     *
     * @param search the search string to set.
     */
    public void setSearch(String search) {
        this.search = search;
    }

}