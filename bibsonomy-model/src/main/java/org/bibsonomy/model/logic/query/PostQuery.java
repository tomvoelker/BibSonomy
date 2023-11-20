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
import org.bibsonomy.common.SortCriteria;
import org.bibsonomy.common.enums.Filter;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.QueryScope;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Resource;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * query used to retrieve posts
 *
 * @author dzo
 * @param <R>
 */
@Getter
@Setter
public class PostQuery<R extends Resource> extends BasicQuery {
	/**
	 * resource type to be shown.
	 */
	private Class<R> resourceClass;

	/**
	 * whether to search locally or using an index shared by several systems or the fulltext search
	 */
	private QueryScope scope = QueryScope.LOCAL;

	/**
	 * grouping tells whom posts are to be shown: the posts of a
	 * user, of a group or of the viewables.
	 */
	private GroupingEntity grouping = GroupingEntity.ALL;

	/**
	 * name of the grouping. if grouping is user, then its the
	 * username. if grouping is set to {@link GroupingEntity#ALL},
	 * then its an empty string!
	 */
	private String groupingName;

	/**
	 * a set of tags. remember to parse special tags like
	 * ->[tagname], -->[tagname] and <->[tagname]. see documentation.
	 *  if the parameter is not used, its an empty list
	 */
	private List<String> tags;

	/**
	 * hash value of a resource, if one would like to get a list of
	 * all posts belonging to a given resource. if unused, its empty
	 * but not null.
	 */
	private String hash;

	/**
	 * filter for the retrieved posts
	 */
	private Set<Filter> filters;

	/**
	 * if given, only posts that have been created after (inclusive) startDate are returned
	 */
	private Date startDate;

	/**
	 * if given, only posts that have been created before (inclusive) endDate are returned
	 */
	private Date endDate;

	/**
	 * flag to retrieve posts where the person names are not assigned to a person
	 */
	private boolean onlyIncludeAuthorsWithoutPersonId = false;

	private List<PersonName> personNames;

	/**
	 * get all publications assigned to persons of the specified college
	 */
	private String college;

	/**
	 * list of sort criterion and ascending/descending sorting
	 */
	private List<SortCriteria> sortCriteria;

	/**
	 * default constructor
	 * @param resourceClass
	 */
	public PostQuery(final Class<R> resourceClass) {
		this.resourceClass = resourceClass;
	}

}
