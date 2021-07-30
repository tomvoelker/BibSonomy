package org.bibsonomy.webapp.command.actions;

import org.bibsonomy.model.cris.Project;
import org.bibsonomy.webapp.command.BaseCommand;

/**
 * command for deleting projects
 *
 * @author tko
 */
public class DeleteProjectCommand extends BaseCommand {

	private String projectIdToDelete;

	private Project project;

	/**
	 * @return
	 */
	public String getProjectIdToDelete() {
		return projectIdToDelete;
	}

	/**
	 * @param projectIdToDelete
	 */
	public void setProjectIdToDelete(String projectIdToDelete) {
		this.projectIdToDelete = projectIdToDelete;
	}

	/**
	 * @return
	 */
	public Project getProject() {
		return project;
	}

	/**
	 * @param project
	 */
	public void setProject(Project project) {
		this.project = project;
	}
}
