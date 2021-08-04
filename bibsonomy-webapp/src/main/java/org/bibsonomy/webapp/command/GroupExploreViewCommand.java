package org.bibsonomy.webapp.command;

import java.util.List;

import org.bibsonomy.model.Group;

public class GroupExploreViewCommand extends SimpleResourceViewCommand {

    private String requestedGroup;
    private Group group;

    private String search;
    private List<String> selectedTags;

    public String getRequestedGroup() {
        return requestedGroup;
    }

    public void setRequestedGroup(String requestedGroup) {
        this.requestedGroup = requestedGroup;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public List<String> getSelectedTags() {
        return selectedTags;
    }

    public void setSelectedTags(List<String> selectedTags) {
        this.selectedTags = selectedTags;
    }
}
