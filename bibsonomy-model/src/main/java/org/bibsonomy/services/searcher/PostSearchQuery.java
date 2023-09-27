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
package org.bibsonomy.services.searcher;

import lombok.Getter;
import lombok.Setter;
import org.bibsonomy.model.SystemTag;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.logic.query.PostQuery;

import java.util.List;

/**
 * adds search specific query fields to the post query
 *
 * only use this query when calling the post search directly
 * for all other queries use the {@link PostQuery}
 *
 * @author dzo
 * @param <R>
 */
@Getter
@Setter
public class PostSearchQuery<R extends Resource> extends PostQuery<R> {

	/**
	 * default constructor
	 */
	public PostSearchQuery() {
		super(null);
	}

	/**
	 * default constructor
	 *
	 * @param resourceClass
	 */
	public PostSearchQuery(final Class<R> resourceClass) {
		super(resourceClass);
	}

	/**
	 * copy constructor for {@link PostQuery}
	 * @param other
	 */
	public PostSearchQuery(final PostQuery<R> other) {
		super(other.getResourceClass());
		this.setSearch(other.getSearch());
		this.setScope(other.getScope());
		this.setGrouping(other.getGrouping());
		this.setGroupingName(other.getGroupingName());
		this.setTags(other.getTags());
		this.setHash(other.getHash());
		this.setFilters(other.getFilters());
		this.setStartDate(other.getStartDate());
		this.setEndDate(other.getEndDate());
		this.setChangeDate(other.getChangeDate());
		this.setOnlyIncludeAuthorsWithoutPersonId(other.isOnlyIncludeAuthorsWithoutPersonId());
		this.setPersonNames(other.getPersonNames());
		this.setCollege(other.getCollege());
		this.setStart(other.getStart());
		this.setEnd(other.getEnd());
		this.setSortCriteria(other.getSortCriteria());
	}

	private List<String> requestedRelationNames;

	/** extracted from the system tag title */
	private String titleSearchTerms;

	/** extracted from the system tag author */
	private String authorSearchTerms;

	/** extracted from the system tag not */
	private List<String> negatedTags;

	private String year;

	private String firstYear;

	private String lastYear;

	private String entryType;

	private String bibtexKey;

	private List<SystemTag> systemTags;

}
