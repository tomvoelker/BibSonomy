package org.bibsonomy.webapp.command;

import org.bibsonomy.model.cris.Project;

/**
 * Command for a project details page
 *
 * @author dzo
 */
public class ProjectPageCommand extends BaseCommand {

	private String requestedProjectId;

	private Project project;

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
