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
