/**
 * BibSonomy-Webapp - The web application for BibSonomy.
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
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.command.cris;

import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.enums.ProjectSortKey;
import org.bibsonomy.model.enums.ProjectStatus;
import org.bibsonomy.webapp.command.EntitySearchAndFilterCommand;
import org.bibsonomy.webapp.command.ListCommand;

/**
 * command to query projects
 *
 * @author dzo
 */
public class ProjectsPageCommand extends EntitySearchAndFilterCommand {

	private final ListCommand<Project> projects = new ListCommand<>(this);

	private ProjectStatus projectStatus = ProjectStatus.RUNNING;

	private ProjectSortKey projectSortKey = ProjectSortKey.TITLE;

	/**
	 * @return the projectOrder
	 */
	public ProjectSortKey getProjectOrder() {
		return projectSortKey;
	}

	/**
	 * @param projectSortKey the projectOrder to set
	 */
	public void setProjectOrder(ProjectSortKey projectSortKey) {
		this.projectSortKey = projectSortKey;
	}

	/**
	 * @return the projectStatus
	 */
	public ProjectStatus getProjectStatus() {
		return projectStatus;
	}

	/**
	 * @param projectStatus the projectStatus to set
	 */
	public void setProjectStatus(ProjectStatus projectStatus) {
		this.projectStatus = projectStatus;
	}

	/**
	 * @return the projects
	 */
	public ListCommand<Project> getProjects() {
		return projects;
	}
}
