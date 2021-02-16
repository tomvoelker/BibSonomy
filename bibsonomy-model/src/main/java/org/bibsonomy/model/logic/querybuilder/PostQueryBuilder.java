package org.bibsonomy.model.logic.querybuilder;

import org.bibsonomy.common.SortCriteria;
import org.bibsonomy.common.enums.Filter;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.QueryScope;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.logic.query.PostQuery;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * post query builder
 *
 * @author pda
 */
public class PostQueryBuilder {
	private QueryScope scope;
	private GroupingEntity grouping;
	private String groupingName;
	private List<String> tags;
	private String hash;
	private Set<Filter> filters;
	private Date startDate;
	private Date endDate;
	private boolean onlyIncludeAuthorsWithoutPersonId;
	private List<PersonName> personNames;
	private String college;
	private String search;
	private int start = 0;
	private int end = 10;

	private List<SortCriteria> sortCriteria;

	public <R extends Resource> PostQuery<R> createPostQuery(Class<R> resourceClass) {
		final PostQuery<R> postQuery = new PostQuery<>(resourceClass);
		postQuery.setSearch(search);
		postQuery.setScope(scope);
		postQuery.setGrouping(grouping);
		postQuery.setGroupingName(groupingName);
		postQuery.setTags(tags);
		postQuery.setHash(hash);
		postQuery.setFilters(filters);
		postQuery.setStartDate(startDate);
		postQuery.setEndDate(endDate);
		postQuery.setPersonNames(personNames);
		postQuery.setOnlyIncludeAuthorsWithoutPersonId(onlyIncludeAuthorsWithoutPersonId);
		postQuery.setStart(start);
		postQuery.setEnd(end);
		postQuery.setCollege(this.college);
		postQuery.setSortCriteriums(this.sortCriteria);
		return postQuery;
	}

	public PostQueryBuilder setEnd(int end) {
		this.end = end;
		return this;
	}

	public PostQueryBuilder setStart(int start) {
		this.start = start;
		return this;
	}

	public PostQueryBuilder setSearch(String search) {
		this.search = search;
		return this;
	}

	public PostQueryBuilder setScope(QueryScope scope) {
		this.scope = scope;
		return this;
	}

	public PostQueryBuilder setGrouping(GroupingEntity grouping) {
		this.grouping = grouping;
		return this;
	}

	public PostQueryBuilder setGroupingName(String groupingName) {
		this.groupingName = groupingName;
		return this;
	}

	public PostQueryBuilder setTags(List<String> tags) {
		this.tags = tags;
		return this;
	}

	public PostQueryBuilder setHash(String hash) {
		this.hash = hash;
		return this;
	}

	public PostQueryBuilder setFilters(Set<Filter> filters) {
		this.filters = filters;
		return this;
	}

	public PostQueryBuilder setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}

	public PostQueryBuilder setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}

	public PostQueryBuilder setOnlyIncludeAuthorsWithoutPersonId(boolean onlyIncludeAuthorsWithoutPersonId) {
		this.onlyIncludeAuthorsWithoutPersonId = onlyIncludeAuthorsWithoutPersonId;
		return this;
	}

	public PostQueryBuilder college(final String college) {
		this.college = college;
		return this;
	}

	public PostQueryBuilder setPersonNames(List<PersonName> personNames) {
		this.personNames = personNames;
		return this;
	}

	/**
	 * @param sortCriteria the sortCriteriums to set
	 */
	public PostQueryBuilder setSortCriteriums(List<SortCriteria> sortCriteria) {
		this.sortCriteria = sortCriteria;
		return this;
	}
}