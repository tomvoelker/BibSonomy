package org.bibsonomy.model.logic.querybuilder;

import org.bibsonomy.common.enums.Prefix;
import org.bibsonomy.model.enums.GroupOrder;
import org.bibsonomy.model.logic.query.GroupQuery;

/**
 * builder for {@link GroupQuery}
 *
 * @author pda
 */
public class GroupQueryBuilder {
	private String search;
	private int start = 0;
	private int end = 10;

	private GroupOrder groupOrder = GroupOrder.GROUP_NAME;
	private Prefix prefix;
	private boolean pending;
	private String userName;

	private String externalId;
	private Boolean organization;

	public GroupQueryBuilder order(final GroupOrder order) {
		this.groupOrder = order;
		return this;
	}

	public GroupQueryBuilder setSearch(final String search) {
		this.search = search;
		return this;
	}

	public GroupQueryBuilder setOrganization(final Boolean organization) {
		this.organization = organization;
		return this;
	}

	public GroupQueryBuilder setPrefix(final Prefix prefix) {
		this.prefix = prefix;
		return this;
	}

	public GroupQueryBuilder setPending(boolean pending) {
		this.pending = pending;
		return this;
	}

	public GroupQueryBuilder setUserName(String userName) {
		this.userName = userName;
		return this;
	}

	public GroupQueryBuilder setStart(int start) {
		this.start = start;
		return this;
	}

	public GroupQueryBuilder setEnd(int end) {
		this.end = end;
		return this;
	}

	public GroupQueryBuilder setExternalId(String externalId) {
		this.externalId = externalId;
		return this;
	}

	public GroupQuery createGroupQuery() {
		return new GroupQuery(this.search, this.groupOrder, this.prefix, this.pending, this.userName, this.externalId, this.organization, this.start, this.end);
	}
}