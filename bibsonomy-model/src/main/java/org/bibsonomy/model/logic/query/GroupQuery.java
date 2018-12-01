package org.bibsonomy.model.logic.query;

/**
 * Specifies a group query.
 *
 * Depending on the settings, the query will be handled differently.
 *
 */
public class GroupQuery implements Query {

    private boolean pending;
    private String userName;
    private int start, end;
    private String externalId;

    /**
     * Creates a group query.
     *
     * @param pending if set to <code>true</code> this query will retrieve pending groups, otherwise only activated groups will be retrieved.
     * @param userName if set the query is restricted to groups created by the user (applies only to pending groups).
     * @param start start index of the retrieved result set.
     * @param end end index of the retrieved result set.
     * @param externalId if a valid non-empty string is supplied, the query will lookup the group with the supplied external id.
     */
    public GroupQuery(boolean pending, String userName, int start, int end, String externalId) {
        this.pending = pending;
        this.userName = userName;
        this.start = start;
        this.end = end;
        this.externalId = externalId;
    }

    public boolean isPending() {
        return pending;
    }

    public String getUserName() {
        return userName;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public String getExternalId() {
        return externalId;
    }
}
