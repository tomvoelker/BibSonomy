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
import org.bibsonomy.webapp.command.BaseCommand;

/**
 * Command for a project details page
 *
 * @author dzo
 */
public class ProjectPageCommand extends BaseCommand {

	private String requestedProjectId;

	private Project project;

	private String members;

	/**
	 * @return
	 */
	public String getMembers() {
		return members;
	}

	/**
	 * @param members
	 */
	public void setMembers(String members) {
		this.members = members;
	}

	/**
	 * @return the requestedProjectId
	 */
	public String getRequestedProjectId() {
		return requestedProjectId;
	}

	/**
	 * @param requestedProjectId the requestedProjectId to set
	 */
	public void setRequestedProjectId(String requestedProjectId) {
		this.requestedProjectId = requestedProjectId;
	}

	/**
	 * @return the project
	 */
	public Project getProject() {
		return project;
	}

	/**
	 * @param project the project to set
	 */
	public void setProject(Project project) {
		this.project = project;
	}
}
