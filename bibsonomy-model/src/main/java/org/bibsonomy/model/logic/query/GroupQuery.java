package org.bibsonomy.model.logic.query;

import org.bibsonomy.common.enums.Prefix;
import org.bibsonomy.common.enums.SortOrder;
import org.bibsonomy.model.enums.GroupOrder;
import org.bibsonomy.model.logic.querybuilder.BasicQueryBuilder;

/**
 * Specifies a group query.
 * <p>
 * Depending on the settings, the query will be handled differently.
 *
 * @author ada, pda
 */
public class GroupQuery extends BasicQuery {

	private final GroupOrder groupOrder;
	/**
	 * the sort order of the order
	 */
	private final SortOrder sortOrder;
	private final boolean pending;
	private final String userName;
	private final String externalId;
	private final Prefix prefix;
	private final Boolean organization;

	/**
	 * Creates a group query.
	 *
	 * @param search         search terms for the full text search
	 * @param usePrefixMatch flag to indicate that the last token should be matched as a prefix
	 * @param phraseMatch    the search terms represent a phrase where the tokens should appear in the order entered
	 * @param groupOrder     the order of the found groups
	 * @param sortOrder      the sort order of the order
	 * @param pending        if set to <code>true</code> this query will retrieve pending groups, otherwise only activated groups will be retrieved.
	 * @param userName       if set the query is restricted to groups created by the user (applies only to pending groups).
	 * @param externalId     if a valid non-empty string is supplied, the query will lookup the group with the supplied external id.
	 * @param organization   if set only organizations or non organizations should be returned
	 * @param start          start index of the retrieved result set.
	 * @param end            end index of the retrieved result set.
	 */
	public GroupQuery(String search, boolean usePrefixMatch, boolean phraseMatch, GroupOrder groupOrder, SortOrder sortOrder, Prefix prefix, boolean pending, String userName, String externalId, Boolean organization, int start, int end) {
		super();
		this.setSearch(search);
		this.setUsePrefixMatch(usePrefixMatch);
		this.setPhraseMatch(phraseMatch);
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

	public static GroupQueryBuilder builder() {
		return new GroupQueryBuilder();
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

	/**
	 * group query builder
	 */
	public final static class GroupQueryBuilder extends BasicQueryBuilder<GroupQueryBuilder> {
		private GroupOrder groupOrder = GroupOrder.GROUP_NAME;
		private SortOrder sortOrder = SortOrder.ASC;
		private Prefix prefix;
		private boolean pending;
		private String userName;

		private String externalId;
		private Boolean organization;

		/**
		 * @param order the group order
		 * @return the group builder
		 */
		public GroupQueryBuilder order(final GroupOrder order) {
			this.groupOrder = order;
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
		public GroupQueryBuilder organization(final Boolean organization) {
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

		/**
		 * @param pending if pending groups should be queried
		 * @return the group builder
		 */
		public GroupQueryBuilder pending(boolean pending) {
			this.pending = pending;
			return this;
		}

		/**
		 * @param userName the user name to query (for pending groups)
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
			return new GroupQuery(search, this.usePrefixMatch, this.phraseMatch, groupOrder, sortOrder, prefix, pending,
							userName, externalId, organization, start, end);
		}
	}
}
