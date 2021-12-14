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
    private String realnameSearch;

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

    public GroupQueryBuilder realnameSearch(final String realnameSearch) {
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