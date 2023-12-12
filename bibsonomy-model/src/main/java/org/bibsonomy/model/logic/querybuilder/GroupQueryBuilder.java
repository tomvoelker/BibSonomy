/**
 * BibSonomy-Model - Java- and JAXB-Model.
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
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.model.logic.querybuilder;

import java.util.Set;

import org.bibsonomy.common.enums.Prefix;
import org.bibsonomy.common.enums.SortOrder;
import org.bibsonomy.model.enums.GroupSortKey;
import org.bibsonomy.model.logic.query.GroupQuery;

/**
 * group query builder
 */
public class GroupQueryBuilder extends BasicQueryBuilder<GroupQueryBuilder> {
    private GroupSortKey groupSortKey = GroupSortKey.GROUP_NAME;
    private SortOrder sortOrder = SortOrder.ASC;
    private Prefix prefix;
    private Set<String> realnameSearch;

    private String userName;
    private String externalId;

    private boolean organization;
    private boolean pending;

    /**
     * @param sortKey the group sort key
     * @return the group builder
     */
    public GroupQueryBuilder sortKey(final GroupSortKey sortKey) {
        this.groupSortKey = sortKey;
        return this;
    }

    /**
     * @param sortOrder the sort order to set
     * @return the group builder
     */
    public GroupQueryBuilder sortOrder(final SortOrder sortOrder) {
        this.sortOrder = sortOrder;
        return this;
    }

    /**
     * @param organization if only organizations should be retrieved
     * @return the group builder
     */
    public GroupQueryBuilder organization(final boolean organization) {
        this.organization = organization;
        return this;
    }

    /**
     * the prefix of the group name
     * @param prefix the prefix
     * @return the group builder
     */
    public GroupQueryBuilder prefix(final Prefix prefix) {
        this.prefix = prefix;
        return this;
    }

    public GroupQueryBuilder realnameSearch(final Set<String> realnameSearch) {
        this.realnameSearch = realnameSearch;
        return this;
    }

    /**
     * @param pending if pending groups should be queried
     * @return the group builder
     */
    public GroupQueryBuilder pending(final boolean pending) {
        this.pending = pending;
        return this;
    }

    /**
     * @param userName query all groups username is member of or has created for pending groups
     * @return the group builder
     */
    public GroupQueryBuilder userName(final String userName) {
        this.userName = userName;
        return this;
    }

    /**
     * @param externalId the external id of the organization
     * @return the group builder
     */
    public GroupQueryBuilder externalId(final String externalId) {
        this.externalId = externalId;
        return this;
    }

    @Override
    protected GroupQueryBuilder builder() {
        return this;
    }

    /**
     * builds the group query
     * @return the group query
     */
    public GroupQuery build() {
        final GroupQuery groupQuery = new GroupQuery();
        groupQuery.setSearch(this.search);
        groupQuery.setUsePrefixMatch(this.usePrefixMatch);
        groupQuery.setPrefix(this.prefix);
        groupQuery.setRealnameSearch(this.realnameSearch);
        groupQuery.setGroupSortKey(this.groupSortKey);
        groupQuery.setSortOrder(this.sortOrder);
        groupQuery.setUserName(this.userName);
        groupQuery.setExternalId(this.externalId);
        groupQuery.setOrganization(this.organization);
        groupQuery.setPending(this.pending);
        groupQuery.setStart(this.start);
        groupQuery.setEnd(this.end);

        return groupQuery;
    }
}