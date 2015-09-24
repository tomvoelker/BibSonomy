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

import java.util.HashSet;
import java.util.Set;

import org.bibsonomy.model.enums.PersonResourceRelationType;

/**
 * TODO: add documentation to this class
 *
 * @author jensi
 */
public abstract class AbstractSuggestionQueryBuilder<T extends AbstractSuggestionQueryBuilder<?>> {
	private final String query;
	private boolean withNonEntityPersons;
	private boolean withEntityPersons;
	private Set<PersonResourceRelationType> relationTypes = new HashSet<>();
	
	/**
	 * @param query any combination of title, author-name, year, school
	 */
	public AbstractSuggestionQueryBuilder(String query) {
		this.query = query;
	}
	
	protected abstract T getThis();
	
	public T withNonEntityPersons(boolean withNonEntityPersons) {
		this.withNonEntityPersons = withNonEntityPersons;
		return getThis();
	}
	
	public T withEntityPersons(boolean withEntityPersons) {
		this.withEntityPersons = withEntityPersons;
		return getThis();
	}
	
	public T withRelationType(PersonResourceRelationType... relationTypes) {
		for (PersonResourceRelationType relationType : relationTypes) {
			this.relationTypes.add(relationType);
		}
		return getThis();
	}

	public boolean isWithNonEntityPersons() {
		return this.withNonEntityPersons;
	}

	public boolean isWithEntityPersons() {
		return this.withEntityPersons;
	}

	public Set<PersonResourceRelationType> getRelationTypes() {
		return this.relationTypes;
	}

	public String getQuery() {
		return this.query;
	}
}
