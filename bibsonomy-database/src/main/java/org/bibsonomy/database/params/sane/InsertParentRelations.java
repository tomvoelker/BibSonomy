package org.bibsonomy.database.params.sane;

public class InsertParentRelations {

    private int parentGroupId;
    private int childGroupId;

    public InsertParentRelations(int parentGroupId, int childGroupId) {
        this.parentGroupId = parentGroupId;
        this.childGroupId = childGroupId;
    }

    public int getParentGroupId() {
        return parentGroupId;
    }

    public void setParentGroupId(int parentGroupId) {
        this.parentGroupId = parentGroupId;
    }

    public int getChildGroupId() {
        return childGroupId;
    }

    public void setChildGroupId(int childGroupId) {
        this.childGroupId = childGroupId;
    }
}
