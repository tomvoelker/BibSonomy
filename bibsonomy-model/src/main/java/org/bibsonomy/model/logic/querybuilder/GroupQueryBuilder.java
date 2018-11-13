package org.bibsonomy.model.logic.querybuilder;

import org.bibsonomy.model.logic.query.GroupQuery;

public class GroupQueryBuilder {
    private boolean pending;
    private String userName;
    private int start = -1;
    private int end = -1;
    private String externalId;

    public GroupQueryBuilder setPending(boolean pending) {
        this.pending = pending;
        return this;
    }

    public GroupQueryBuilder setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public GroupQueryBuilder setStart(int start) {
        this.start = start;
        return this;
    }

    public GroupQueryBuilder setEnd(int end) {
        this.end = end;
        return this;
    }

    public GroupQueryBuilder setExternalId(String externalId) {
        this.externalId = externalId;
        return this;
    }

    public GroupQuery createGroupQuery() {
        return new GroupQuery(pending, userName, start, end, externalId);
    }
}