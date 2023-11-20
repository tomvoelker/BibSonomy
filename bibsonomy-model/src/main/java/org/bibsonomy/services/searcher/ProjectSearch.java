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

import org.bibsonomy.model.User;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.logic.query.ProjectQuery;
import org.bibsonomy.model.statistics.Statistics;
import org.bibsonomy.util.object.FieldDescriptor;

import java.util.List;
import java.util.Set;

/**
 * search interface to search for {@link Project}s using the full text search
 *
 * @author dzo
 */
public interface ProjectSearch {

	/**
	 * @param loggedinUser the logged in user (full details)
	 * @param query the query to filter the projects
	 * @return all matching projects
	 */
	List<Project> getProjects(final User loggedinUser, final ProjectQuery query);

	/**
	 * stats about the projects
	 * @param loggedinUser
	 * @param query
	 * @return
	 */
	Statistics getStatistics(final User loggedinUser, final ProjectQuery query);

	/**
	 * returns all values for the specified field
	 * @param fieldDescriptor
	 * @return
	 */
	<E> Set<E> getDistinctFieldValues(final FieldDescriptor<Project, E> fieldDescriptor);
}