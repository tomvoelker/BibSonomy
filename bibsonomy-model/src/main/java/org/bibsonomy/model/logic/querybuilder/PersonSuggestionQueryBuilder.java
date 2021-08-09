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

/**
 * TODO: add documentation to this class
 *
 * @author jensi
 */
public class PersonSuggestionQueryBuilder extends AbstractSuggestionQueryBuilder<PersonSuggestionQueryBuilder> {

	private boolean preferUnlinked;
	private boolean allowNamesWithoutEntities = true;

	/**
	 * @param query any combination of title, author-name, year, school
	 */
	public PersonSuggestionQueryBuilder(String query) {
		super(query);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.querybuilder.AbstractSuggestionQueryBuilder#getThis()
	 */
	@Override
	protected PersonSuggestionQueryBuilder getThis() {
		return this;
	}

	/**
	 * @param preferUnlinked
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
	public PersonSuggestionQueryBuilder allowNamesWithoutEntities(boolean allowNamesWithoutEntities) {
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
