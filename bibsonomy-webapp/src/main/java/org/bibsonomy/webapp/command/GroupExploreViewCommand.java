package org.bibsonomy.webapp.command;

import java.util.List;
import java.util.Map;

import org.bibsonomy.common.Pair;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.extra.SearchFilterElement;

public class GroupExploreViewCommand extends SimpleResourceViewCommand {

    private String requestedGroup;
    private Group group;

    private String search;
    private String filters;
    private List<SearchFilterElement> entrytypeFilters;
    private List<SearchFilterElement> yearFilters;
    private List<SearchFilterElement> authorFilters;

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

    public String getFilters() {
        return filters;
    }

    public void setFilters(String filters) {
        this.filters = filters;
    }

    public List<SearchFilterElement> getEntrytypeFilters() {
        return entrytypeFilters;
    }

    public void setEntrytypeFilters(List<SearchFilterElement> entrytypeFilters) {
        this.entrytypeFilters = entrytypeFilters;
    }

    public List<SearchFilterElement> getYearFilters() {
        return yearFilters;
    }

    public void setYearFilters(List<SearchFilterElement> yearFilters) {
        this.yearFilters = yearFilters;
    }

    public List<SearchFilterElement> getAuthorFilters() {
        return authorFilters;
    }

    public void setAuthorFilters(List<SearchFilterElement> authorFilters) {
        this.authorFilters = authorFilters;
    }
}
