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
import org.bibsonomy.common.enums.Prefix;
import org.bibsonomy.common.enums.SortOrder;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.enums.ProjectSortKey;
import org.bibsonomy.model.enums.ProjectStatus;
import org.bibsonomy.model.logic.querybuilder.BasicQueryBuilder;

import java.util.Date;

/**
 * the project query to retrieve projects from the logic
 *
 * @author dzo
 */
@Getter
public class ProjectQuery extends BasicQuery {

	private final Prefix prefix;
	/**
	 * the sort key of the projects
	 */
	private final ProjectSortKey sortKey;
	/**
	 * the sort order of the projects
	 */
	private final SortOrder sortOrder;
	/**
	 * the project status
	 */
	private final ProjectStatus projectStatus;
	/**
	 * the type of the project
	 */
	private final String type;
	private final String sponsor;
	private final String internalId;
	private final Date startDate;
	private final Date endDate;
	private final Person person;
	private final Group organization;

	/**
	 * the constructor
	 *
	 * @param sortKey
	 * @param sortOrder
	 * @param projectStatus
	 * @param type
	 * @param start
	 * @param end
	 * @param externalId
	 * @param startDate
	 * @param endDate
	 * @param person
	 * @param organization
	 */
	public ProjectQuery(final String search, final Prefix prefix, final ProjectSortKey sortKey, SortOrder sortOrder,
						   ProjectStatus projectStatus, String type, String sponsor, int start, int end,
						   String externalId, Date startDate, Date endDate, Person person, Group organization) {
		this.person = person;
		this.organization = organization;
		this.setSearch(search);
		this.setStart(start);
		this.setEnd(end);

		this.prefix = prefix;
		this.startDate = startDate;
		this.endDate = endDate;
		this.sortKey = sortKey;
		this.sortOrder = sortOrder;
		this.projectStatus = projectStatus;
		this.type = type;
		this.internalId = externalId;
		this.sponsor = sponsor;
	}

}
