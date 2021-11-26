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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bibsonomy.model.Group;
import org.bibsonomy.model.extra.SearchFilterElement;

/**
 * @author kchoong
 */
public class GroupExploreViewCommand extends SimpleResourceViewCommand {

    // Requested group id
    private String requestedGroup;

    // Group
    private Group group;

    // Search string
    private String search;

    // Search filter map with attribute field as key
    private Map<String, List<SearchFilterElement>> filterMap = new HashMap<>();

    public void addFilters(String field, List<SearchFilterElement> filters) {
        filterMap.put(field, filters);
    }

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

    public Map<String, List<SearchFilterElement>> getFilterMap() {
        return filterMap;
    }

    public void setFilterMap(Map<String, List<SearchFilterElement>> filterMap) {
        this.filterMap = filterMap;
    }
}
