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
package org.bibsonomy.model.logic;

import org.bibsonomy.common.JobResult;
import org.bibsonomy.model.cris.CRISLink;
import org.bibsonomy.model.cris.Linkable;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.logic.query.ProjectQuery;

import java.util.List;

/**
 * defines all interactions that are required for the
 * Current research information system
 *
 * @author dzo
 */
public interface CRISLogicInterface {

	/**
	 * retrieves a filterable list of projects.
	 * @param query
	 * @return
	 */
	List<Project> getProjects(ProjectQuery query);

	/**
	 * Returns details to a project. A project is uniquely identified by the external project id.
	 * @param projectId
	 * @return
	 */
	Project getProjectDetails(final String projectId);

	/**
	 * creates a new project
	 * @param project
	 * @return
	 */
	JobResult createProject(final Project project);

	/**
	 * updates a project identified by it's external project id
	 * @param projectId
	 * @param project
	 * @return
	 */
	JobResult updateProject(final String projectId, final Project project);

	/**
	 * deletes a project identified by it's external project id
	 * @param projectId
	 * @return
	 */
	JobResult deleteProject(final String projectId);

	/**
	 * creates a link between cris entries
	 * @param link
	 * @return
	 */
	JobResult createCRISLink(final CRISLink link);

	/**
	 * updates the link between the two linkable cris entities
	 * @param link
	 * @return the result
	 */
	JobResult updateCRISLink(final CRISLink link);

	/**
	 * deletes the link between the two linkable cris entities
	 * @param source
	 * @param target
	 * @return
	 */
	JobResult deleteCRISLink(final Linkable source, final Linkable target);
}

