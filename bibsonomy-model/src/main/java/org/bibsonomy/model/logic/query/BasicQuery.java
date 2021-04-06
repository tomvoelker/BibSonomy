package org.bibsonomy.model.logic.query;


/**
 * A basic query that supports pagination and searching.
 *
 * @author dzo
 */
public class BasicQuery extends BasicPaginatedQuery implements SearchQuery, Query {

    /** free text search */
    private String search;

    /** the provided search is not complete, e.g. someone is typing and we want a prefix match for the last token */
    private boolean usePrefixMatch = false;

    /** the complete phrase is ordered and the tokens should be matched in this order */
    private boolean phraseMatch = false;

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

    /**
     * @return the usePrefixMatch
     */
    public boolean isUsePrefixMatch() {
        return usePrefixMatch;
    }

    /**
     * @param usePrefixMatch the usePrefixMatch to set
     */
    public void setUsePrefixMatch(boolean usePrefixMatch) {
        this.usePrefixMatch = usePrefixMatch;
    }

    /**
     * @return the phraseMatch
     */
    public boolean isPhraseMatch() {
        return phraseMatch;
    }

    /**
     * @param phraseMatch the phraseMatch to set
     */
    public void setPhraseMatch(boolean phraseMatch) {
        this.phraseMatch = phraseMatch;
    }
}