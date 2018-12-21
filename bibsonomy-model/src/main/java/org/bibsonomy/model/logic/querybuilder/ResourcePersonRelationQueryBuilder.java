/**
 * BibSonomy-Model - Java- and JAXB-Model.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
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

import org.bibsonomy.model.enums.PersonResourceRelationOrder;
import org.bibsonomy.model.enums.PersonResourceRelationType;

/**
 * TODO: add documentation to this class
 * FIXME: (AD) refactor into separate builder and query object classes, add a build method with validation and move validation checks from DBLogic implementations into the build method!
 * @author jensi
 */
public class ResourcePersonRelationQueryBuilder {

	private boolean withPersons;
	private boolean withPosts;
	private boolean withPersonsOfPosts;
	private PersonResourceRelationType relationType;
	private String interhash;
	private Integer authorIndex;
	private String personId;
	private PersonResourceRelationOrder order;
	private boolean groupByInterhash;

	private boolean paginated;
	private int start;
	private int end;

	
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
	
	public ResourcePersonRelationQueryBuilder byInterhash(String interhash) {
		this.interhash = interhash;
		return this;
	}
	
	public ResourcePersonRelationQueryBuilder byPersonId(String personId) {
		this.personId = personId;
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
	
	public ResourcePersonRelationQueryBuilder orderBy(PersonResourceRelationOrder order) {
		this.order = order;
		return this;
	}
	
	public ResourcePersonRelationQueryBuilder groupByInterhash(boolean groupByInterhash) {
		this.groupByInterhash = groupByInterhash;
		return this;
	}


	/**
	 * Retrieve only resources from [<code>start</code>; <code>end</code>).
	 *
	 * @param start index of the first item.
	 * @param end index of the last item.
	 *
	 * @return the builder.
	 */
	public ResourcePersonRelationQueryBuilder fromTo(int start, int end) {
		if (start < 0 || end < 0) {
			throw new IllegalArgumentException(String.format("Indices must be >= 0. start=%d, end=%d", start, end));
		}

		if (start > end) {
			throw new IllegalArgumentException(String.format("start must be <= end: %d > %d", start, end));
		}

		this.paginated = true;
		this.start = start;
		this.end = end;

		return this;
	}

	
	/**
	 * @return the withPersons
	 */
	public boolean isWithPersons() {
		return this.withPersons;
	}

	public String getInterhash() {
		return this.interhash;
	}
	
	/**
	 * @return the relationType
	 */
	public PersonResourceRelationType getRelationType() {
		return this.relationType;
	}
	
	/**
	 * @return the authorIndex
	 */
	public Integer getAuthorIndex() {
		return this.authorIndex;
	}
	
	/**
	 * @return the withPosts
	 */
	public boolean isWithPosts() {
		return this.withPosts;
	}
	
	/**
	 * @return the personId
	 */
	public String getPersonId() {
		return this.personId;
	}

	public PersonResourceRelationOrder getOrder() {
		return this.order;
	}
	
	/**
	 * @return the groupByInterhash
	 */
	public boolean isGroupByInterhash() {
		return this.groupByInterhash;
	}

	public boolean isWithPersonsOfPosts() {
		return this.withPersonsOfPosts;
	}


	/**
	 * Tells whether the query should be paginated.
	 *
	 * @return <code>true</code> if a paginated query should be performed, <code>false</code> otherwise.
	 */
	public boolean isPaginated() {
		return paginated;
	}


	/**
	 * Gets the start index of the page.
	 *
	 * @return the start index.
	 */
	public int getStart() {
		return start;
	}


	/**
	 * Gets the end index of the page.
	 *
	 * @return the end index.
	 */
	public int getEnd() {
		return end;
	}
}
