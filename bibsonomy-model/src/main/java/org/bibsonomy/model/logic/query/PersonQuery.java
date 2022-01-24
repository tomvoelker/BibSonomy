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
package org.bibsonomy.model.logic.query;

import lombok.Getter;
import lombok.Setter;
import org.bibsonomy.common.enums.Prefix;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.enums.PersonSortKey;
import org.bibsonomy.model.extra.AdditionalKey;
import org.bibsonomy.model.logic.querybuilder.PersonSuggestionQueryBuilder;

/**
 * adapter for {@link PersonSuggestionQueryBuilder}
 *
 * FIXME add real person query builder
 * @author dzo
 */
@Getter
@Setter
public class PersonQuery extends PersonSuggestionQueryBuilder implements PaginatedQuery, Query {

	private String college;
	private Prefix prefix;
	private int start = 0;
	private int end = 20;
	private PersonSortKey order;
	/** the organization to filter for */
	private Group organization;
	/** find the person claimed by the specified user */
	private String userName;
	/** additiona person key */
	private AdditionalKey additionalKey;

	/** the query provided is only a prefix, perform a prefix search */
	private boolean usePrefixMatch = false;
	private boolean phraseMatch = false;

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

}
