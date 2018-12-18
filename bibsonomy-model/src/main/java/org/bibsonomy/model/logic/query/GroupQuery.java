package org.bibsonomy.model.logic.query;

import org.bibsonomy.common.enums.Prefix;
import org.bibsonomy.common.enums.SortOrder;
import org.bibsonomy.model.enums.GroupOrder;

/**
 * Specifies a group query.
 *
 * Depending on the settings, the query will be handled differently.
 * @author ada, pda
 */
public class GroupQuery extends BasicQuery {

	private final GroupOrder groupOrder;
	/** the sort order of the order */
	private final SortOrder sortOrder;

	private final boolean pending;
	private final String userName;
	private final String externalId;
	private final Prefix prefix;
	private final Boolean organization;

	/**
	 * Creates a group query.
	 *
	 * @param search search terms for the full text search
	 * @param groupOrder the order of the found groups
	 * @param sortOrder  the sort order of the order
	 * @param pending    if set to <code>true</code> this query will retrieve pending groups, otherwise only activated groups will be retrieved.
	 * @param userName   if set the query is restricted to groups created by the user (applies only to pending groups).
	 * @param externalId if a valid non-empty string is supplied, the query will lookup the group with the supplied external id.
	 * @param organization if set only organizations or non organizations should be returned
	 * @param start      start index of the retrieved result set.
	 * @param end        end index of the retrieved result set.
	 */
	public GroupQuery(final String search, final GroupOrder groupOrder, SortOrder sortOrder, final Prefix prefix,
										final boolean pending, final String userName, final String externalId, final Boolean organization,
										int start, int end) {
		this.setSearch(search);
		this.setStart(start);
		this.setEnd(end);
		this.groupOrder = groupOrder;
		this.sortOrder = sortOrder;
		this.prefix = prefix;
		this.pending = pending;
		this.userName = userName;
		this.externalId = externalId;
		this.organization = organization;
	}

	/**
	 * @return the pending
	 */
	public boolean isPending() {
		return pending;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @return the internalId
	 */
	public String getExternalId() {
		return externalId;
	}

	/**
	 * @return the prefix
	 */
	public Prefix getPrefix() {
		return prefix;
	}

	/**
	 * @return the organization
	 */
	public Boolean getOrganization() {
		return organization;
	}

	/**
	 * @return the groupOrder
	 */
	public GroupOrder getGroupOrder() {
		return groupOrder;
	}

	/**
	 * @return the sortOrder
	 */
	public SortOrder getSortOrder() {
		return sortOrder;
	}
}
