/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.command;

import java.util.List;

import org.bibsonomy.model.Group;
import org.bibsonomy.model.extra.SearchFilterElement;

/**
 * @author kchoong
 */
public class GroupExploreViewCommand extends SimpleResourceViewCommand {

    private String requestedGroup;
    private Group group;

    private String search;

    private String customTagFiltersUrl;
    private List<SearchFilterElement> customTagFilters;

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

    public String getCustomTagFiltersUrl() {
        return customTagFiltersUrl;
    }

    public void setCustomTagFiltersUrl(String customTagFiltersUrl) {
        this.customTagFiltersUrl = customTagFiltersUrl;
    }

    public List<SearchFilterElement> getCustomTagFilters() {
        return customTagFilters;
    }

    public void setCustomTagFilters(List<SearchFilterElement> customTagFilters) {
        this.customTagFilters = customTagFilters;
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
