package org.bibsonomy.webapp.command;

import java.util.List;

import org.bibsonomy.common.Pair;
import org.bibsonomy.model.Group;

public class GroupExploreViewCommand extends SimpleResourceViewCommand {

    private String requestedGroup;
    private Group group;

    private String search;
    private List<String> selectedTags;
    private List<Pair<String, Integer>> entrytypeFilters;
    private List<Pair<String, Integer>> yearFilters;
    private List<Pair<String, Integer>> authorFilters;

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

    public List<Pair<String, Integer>> getEntrytypeFilters() {
        return entrytypeFilters;
    }

    public void setEntrytypeFilters(List<Pair<String, Integer>> entrytypeFilters) {
        this.entrytypeFilters = entrytypeFilters;
    }

    public List<Pair<String, Integer>> getYearFilters() {
        return yearFilters;
    }

    public void setYearFilters(List<Pair<String, Integer>> yearFilters) {
        this.yearFilters = yearFilters;
    }

    public List<Pair<String, Integer>> getAuthorFilters() {
        return authorFilters;
    }

    public void setAuthorFilters(List<Pair<String, Integer>> authorFilters) {
        this.authorFilters = authorFilters;
    }
}
