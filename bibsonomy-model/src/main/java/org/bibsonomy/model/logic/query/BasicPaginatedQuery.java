package org.bibsonomy.model.logic.query;

public abstract class BasicPaginatedQuery implements PaginatedQuery, Query{

    private int start;

    private int end;

    /**
     * Initializes the paginated query with default values start=0, end=10.
     */
    public BasicPaginatedQuery() {
        this(0, 10);
    }

    /**
     * Initializes the paginated query with the given values.
     *
     * @param start the start index.
     * @param end the end index.
     */
    public BasicPaginatedQuery(int start, int end) {
        this.start = start;
        this.end = end;
    }

    /**
     * @return the start
     */
    public int getStart() {
        return start;
    }

    /**
     * @param start the start to set
     */
    public void setStart(int start) {
        this.start = start;
    }

    /**
     * @return the end
     */
    public int getEnd() {
        return end;
    }

    /**
     * @param end the end to set
     */
    public void setEnd(int end) {
        this.end = end;
    }
}
