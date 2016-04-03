/**
 * BibSonomy-Model - Java- and JAXB-Model.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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


/**
 * TODO: add documentation to this class
 *
 * @author jensi
 */
public abstract class PersonSuggestionQueryBuilder extends AbstractSuggestionQueryBuilder<PersonSuggestionQueryBuilder> {
	
	
	private boolean preferUnlinked;
	private boolean allowNamesWithoutEntities = true;

	/**
	 * @param query any combination of title, author-name, year, school
	 */
	public PersonSuggestionQueryBuilder(String query) {
		super(query);
	}
	
	/**
	 * @return the result of the processed query
	 */
	public abstract List<ResourcePersonRelation> doIt();
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.querybuilder.AbstractSuggestionQueryBuilder#getThis()
	 */
	@Override
	protected PersonSuggestionQueryBuilder getThis() {
		return this;
	}

	/**
	 * @param b
	 * @return
	 */
	public PersonSuggestionQueryBuilder preferUnlinked(boolean preferUnlinked) {
		this.preferUnlinked = preferUnlinked;
		return this;
	}

	public boolean isPreferUnlinked() {
		return this.preferUnlinked;
	}

	/**
	 * @param allowNamesWithoutEntities - whether the query response may contain names of bibtex-authors/editors that are not associated to a person entity.
	 * @return this
	 */
	public AbstractSuggestionQueryBuilder<PersonSuggestionQueryBuilder> allowNamesWithoutEntities(boolean allowNamesWithoutEntities) {
		this.allowNamesWithoutEntities = allowNamesWithoutEntities;
		return this;
	}

	/**
	 * @return see {@link #allowNamesWithoutEntities(boolean)}
	 */
	public boolean isAllowNamesWithoutEntities() {
		return this.allowNamesWithoutEntities;
	}
}
