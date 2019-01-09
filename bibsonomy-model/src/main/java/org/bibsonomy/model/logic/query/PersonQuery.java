package org.bibsonomy.model.logic.query;

import org.bibsonomy.common.enums.Prefix;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.enums.PersonOrder;
import org.bibsonomy.model.logic.querybuilder.PersonSuggestionQueryBuilder;

/**
 * adapter for {@link PersonSuggestionQueryBuilder}
 *
 * FIXME add real person query builder
 * @author dzo
 */
public class PersonQuery extends PersonSuggestionQueryBuilder implements PaginatedQuery, Query {

	private String college;
	private Prefix prefix;
	private int start = 0;
	private int end = 20;
	private PersonOrder order;
	/** the organization to filter for */
	private Group organization;
	/** find the person claimed by the specified user */
	private String userName;

	/**
	 * default person query with empty search
	 */
	public PersonQuery() {
		super(null);
	}

	/**
	 * @param query any combination of title, author-name
	 */
	public PersonQuery(String query) {
		super(query);
	}

	/**
	 * @return the college
	 */
	public String getCollege() {
		return college;
	}

	/**
	 * @param college the college to set
	 */
	public void setCollege(String college) {
		this.college = college;
	}

	/**
	 * @return the prefix
	 */
	public Prefix getPrefix() {
		return prefix;
	}

	/**
	 * @param prefix the prefix to set
	 */
	public void setPrefix(Prefix prefix) {
		this.prefix = prefix;
	}

	/**
	 * @return the start
	 */
	@Override
	public int getStart() {
		return start;
	}

	/**
	 * @param start the start to set
	 */
	public void setStart(int start) {
		this.start = start;
	}

	/**
	 * @return the end
	 */
	@Override
	public int getEnd() {
		return end;
	}

	/**
	 * @param end the end to set
	 */
	public void setEnd(int end) {
		this.end = end;
	}

	/**
	 * @return the order
	 */
	public PersonOrder getOrder() {
		return order;
	}

	/**
	 * @param order the order to set
	 */
	public void setOrder(PersonOrder order) {
		this.order = order;
	}

	/**
	 * @return the organization
	 */
	public Group getOrganization() {
		return organization;
	}

	/**
	 * @param organization the organization to set
	 */
	public void setOrganization(Group organization) {
		this.organization = organization;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
}
