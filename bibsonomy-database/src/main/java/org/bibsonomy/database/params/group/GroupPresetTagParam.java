package org.bibsonomy.database.params.group;

import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.model.extra.GroupPresetTag;

public class GroupPresetTagParam extends TagParam {

    private int groupId;
    private String groupName;
    private String description;

    public GroupPresetTagParam() {
    }

    public GroupPresetTagParam(GroupPresetTag presetTag) {
        this.setName(presetTag.getName());
        this.groupId = presetTag.getGroupId();
        this.groupName = presetTag.getGroupName();
        this.description = presetTag.getDescription();
    }

    @Override
    public int getGroupId() {
        return groupId;
    }

    @Override
    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }
}
