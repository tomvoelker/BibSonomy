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

import java.util.Date;

import org.bibsonomy.common.enums.SortOrder;
import org.bibsonomy.model.enums.PersonResourceRelationSortKey;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.model.logic.query.ResourcePersonRelationQuery;

/**
 * builder for a {@link ResourcePersonRelationQuery}
 *
 * @author jensi
 * @author ada
 * @author dzo
 */
public class ResourcePersonRelationQueryBuilder extends BasicPaginatedQueryBuilder<ResourcePersonRelationQueryBuilder> {

	private String personId;
	private String interhash;
	private PersonResourceRelationType relationType;
	private Integer authorIndex;
	private Date beforeChangeDate;
	private Date afterChangeDate;

	private boolean withPersons;
	private boolean withPosts;
	private boolean withPersonsOfPosts;
	private boolean onlyTheses;
	private boolean groupByInterhash;

	private PersonResourceRelationSortKey sortKey;
	private SortOrder sortOrder;

	public ResourcePersonRelationQueryBuilder byPersonId(String personId) {
		this.personId = personId;
		return this;
	}

	public ResourcePersonRelationQueryBuilder byInterhash(String interhash) {
		this.interhash = interhash;
		return this;
	}
	
	public ResourcePersonRelationQueryBuilder byRelationType(PersonResourceRelationType relationType) {
		this.relationType = relationType;
		return this;
	}
	
	public ResourcePersonRelationQueryBuilder byAuthorIndex(Integer authorIndex) {
		this.authorIndex = authorIndex;
		return this;
	}

	public ResourcePersonRelationQueryBuilder beforeChangeDate(Date beforeChangeDate) {
		this.beforeChangeDate = beforeChangeDate;
		return this;
	}

	public ResourcePersonRelationQueryBuilder afterChangeDate(Date afterChangeDate) {
		this.afterChangeDate = afterChangeDate;
		return this;
	}

	/**
	 * @param withPersons whether to initialize the person references in the result objects
	 * @return this builder
	 */
	public ResourcePersonRelationQueryBuilder withPersons(boolean withPersons) {
		this.withPersons = withPersons;
		return this;
	}

	/**
	 * @param withPosts whether to initialize the post references in the result objects
	 * @return this builder
	 */
	public ResourcePersonRelationQueryBuilder withPosts(boolean withPosts) {
		this.withPosts = withPosts;
		return this;
	}

	/**
	 * @param withPersonsOfPosts whether to initialize the nested person relations of the resources of the post references in the result objects
	 * @return this builder
	 */
	public ResourcePersonRelationQueryBuilder withPersonsOfPosts(boolean withPersonsOfPosts) {
		this.withPersonsOfPosts = withPersonsOfPosts;
		return this;
	}

	public ResourcePersonRelationQueryBuilder onlyTheses(boolean onlyTheses) {
		this.onlyTheses = onlyTheses;
		return this;
	}

	public ResourcePersonRelationQueryBuilder groupByInterhash(boolean groupByInterhash) {
		this.groupByInterhash = groupByInterhash;
		return this;
	}
	
	public ResourcePersonRelationQueryBuilder sortBy(PersonResourceRelationSortKey sortKey) {
		this.sortKey = sortKey;
		return this;
	}

	public ResourcePersonRelationQueryBuilder orderBy(SortOrder sortOrder) {
		this.sortOrder = sortOrder;
		return this;
	}


	/**
	 * builds the query
	 * @return the query
	 */
	public ResourcePersonRelationQuery build() {
		return new ResourcePersonRelationQuery(personId, interhash, relationType, authorIndex, beforeChangeDate, afterChangeDate, withPersons,
				withPosts, withPersonsOfPosts, onlyTheses, groupByInterhash, sortKey, sortOrder, start, end);
	}

	@Override
	protected ResourcePersonRelationQueryBuilder builder() {
		return this;
	}
}
