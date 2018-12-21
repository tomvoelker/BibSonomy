package org.bibsonomy.webapp.command;

import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.enums.ProjectOrder;
import org.bibsonomy.model.enums.ProjectStatus;

/**
 * command to query projects
 *
 * @author dzo
 */
public class ProjectsPageCommand extends EntitySearchAndFilterCommand {

	private final ListCommand<Project> projects = new ListCommand<>(this);

	private ProjectStatus projectStatus = ProjectStatus.RUNNING;

	private ProjectOrder projectOrder = ProjectOrder.TITLE;

	/**
	 * @return the projectOrder
	 */
	public ProjectOrder getProjectOrder() {
		return projectOrder;
	}

	/**
	 * @param projectOrder the projectOrder to set
	 */
	public void setProjectOrder(ProjectOrder projectOrder) {
		this.projectOrder = projectOrder;
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
