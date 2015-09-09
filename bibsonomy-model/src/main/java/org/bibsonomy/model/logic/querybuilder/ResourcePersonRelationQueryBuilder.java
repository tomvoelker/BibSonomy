/**
 * BibSonomy-Model - Java- and JAXB-Model.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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

import java.util.List;

import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.enums.PersonResourceRelationType;

/**
 * TODO: add documentation to this class
 *
 * @author jensi
 */
public abstract class ResourcePersonRelationQueryBuilder {
	private boolean withPersons;
	private boolean withPosts;
	private boolean withPersonsOfPosts;
	private PersonResourceRelationType relationType;
	private String interhash;
	private Integer authorIndex;
	private String personId;
	private Order order;
	private boolean groupByInterhash;
	
	
	public static enum Order {
		publicationYear
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
	
	public ResourcePersonRelationQueryBuilder orderBy(Order order) {
		this.order = order;
		return this;
	}
	
	public ResourcePersonRelationQueryBuilder groupByInterhash(boolean groupByInterhash) {
		this.groupByInterhash = groupByInterhash;
		return this;
	}
	
	/**
	 * @return the withPersons
	 */
	public boolean isWithPersons() {
		return this.withPersons;
	}
	
	public abstract List<ResourcePersonRelation> getIt();

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

	public Order getOrder() {
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
}
