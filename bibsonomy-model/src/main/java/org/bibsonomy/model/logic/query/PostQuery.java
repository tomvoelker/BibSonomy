package org.bibsonomy.model.logic.query;

import org.bibsonomy.common.enums.Filter;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.QueryScope;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.enums.Order;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * query used to retrieve posts
 *
 * @author dzo
 * @param <R>
 */
public class PostQuery<R extends Resource> extends BasicQuery {
	private Class<R> resourceClass;

	private QueryScope scope = QueryScope.LOCAL;

	private GroupingEntity grouping = GroupingEntity.ALL;

	private String groupingName;

	private List<String> tags;

	private String hash;

	private Set<Filter> filters;

	private Order order;

	private Date startDate;

	private Date endDate;

	/** flag to retrieve posts where the person names are not assigned to a person */
	private boolean onlyIncludeAuthorsWithoutPersonId = false;

	private List<PersonName> personNames;

	/**
	 * default constructor
	 * @param resourceClass
	 */
	public PostQuery(Class<R> resourceClass) {
		this.resourceClass = resourceClass;
	}

	/**
	 * @return the scope
	 */
	public QueryScope getScope() {
		return scope;
	}

	/**
	 * @param scope the scope to set
	 */
	public void setScope(QueryScope scope) {
		this.scope = scope;
	}

	/**
	 * @return the grouping
	 */
	public GroupingEntity getGrouping() {
		return grouping;
	}

	/**
	 * @param grouping the grouping to set
	 */
	public void setGrouping(GroupingEntity grouping) {
		this.grouping = grouping;
	}

	/**
	 * @return the groupingName
	 */
	public String getGroupingName() {
		return groupingName;
	}

	/**
	 * @param groupingName the groupingName to set
	 */
	public void setGroupingName(String groupingName) {
		this.groupingName = groupingName;
	}

	/**
	 * @return the tags
	 */
	public List<String> getTags() {
		return tags;
	}

	/**
	 * @param tags the tags to set
	 */
	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	/**
	 * @return the hash
	 */
	public String getHash() {
		return hash;
	}

	/**
	 * @param hash the hash to set
	 */
	public void setHash(String hash) {
		this.hash = hash;
	}

	/**
	 * @return the filters
	 */
	public Set<Filter> getFilters() {
		return filters;
	}

	/**
	 * @param filters the filters to set
	 */
	public void setFilters(Set<Filter> filters) {
		this.filters = filters;
	}

	/**
	 * @return the order
	 */
	public Order getOrder() {
		return order;
	}

	/**
	 * @param order the order to set
	 */
	public void setOrder(Order order) {
		this.order = order;
	}

	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the resourceClass
	 */
	public Class<R> getResourceClass() {
		return resourceClass;
	}

	/**
	 * @param resourceClass the resourceClass to set
	 */
	public void setResourceClass(Class<R> resourceClass) {
		this.resourceClass = resourceClass;
	}

	/**
	 * @return the onlyIncludeAuthorsWithoutPersonId
	 */
	public boolean isOnlyIncludeAuthorsWithoutPersonId() {
		return onlyIncludeAuthorsWithoutPersonId;
	}

	/**
	 * @param onlyIncludeAuthorsWithoutPersonId the onlyIncludeAuthorsWithoutPersonId to set
	 */
	public void setOnlyIncludeAuthorsWithoutPersonId(boolean onlyIncludeAuthorsWithoutPersonId) {
		this.onlyIncludeAuthorsWithoutPersonId = onlyIncludeAuthorsWithoutPersonId;
	}

	/**
	 * @return the personNames
	 */
	public List<PersonName> getPersonNames() {
		return personNames;
	}

	/**
	 * @param personNames the personNames to set
	 */
	public void setPersonNames(List<PersonName> personNames) {
		this.personNames = personNames;
	}
}
