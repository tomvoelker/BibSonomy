package org.bibsonomy.model.logic.query;

public class GroupQuery implements Query {

    private boolean pending;
    private String userName;
    private int start, end;
    private String externalId;

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
