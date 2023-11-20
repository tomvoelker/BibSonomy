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

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.common.SortCriteria;
import org.bibsonomy.common.enums.*;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.logic.query.PostQuery;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * post query builder
 *
 * @author pda
 */
public class PostQueryBuilder extends BasicQueryBuilder<PostQueryBuilder> {
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

	private List<SortCriteria> sortCriteria;

	public <R extends Resource> PostQuery<R> createPostQuery(final Class<R> resourceClass) {
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
		postQuery.setSortCriteria(this.sortCriteria);
		return postQuery;
	}

	@Override
	protected PostQueryBuilder builder() {
		return this;
	}

	public PostQueryBuilder searchAndSortCriteria(final String search, SortCriteria defaultSortCriteria) {
		final List<SortCriteria> sortCriteria = new LinkedList<>();
		if (present(search)) {
			sortCriteria.add(new SortCriteria(SortKey.RANK, SortOrder.ASC));
		} else {
			sortCriteria.add(defaultSortCriteria);
		}
		this.setSortCriteria(sortCriteria);
		return this.search(search);
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
	 * @param sortCriteria the sortCriteria to set
	 */
	public PostQueryBuilder setSortCriteria(List<SortCriteria> sortCriteria) {
		this.sortCriteria = sortCriteria;
		return this;
	}
}