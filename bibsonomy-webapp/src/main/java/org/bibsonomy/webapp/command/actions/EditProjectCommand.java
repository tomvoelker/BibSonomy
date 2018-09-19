package org.bibsonomy.webapp.command.actions;

import org.bibsonomy.model.cris.Project;
import org.bibsonomy.webapp.command.BaseCommand;


public class EditProjectCommand extends BaseCommand {

	private String projectIdToUpdate;

	private Project project;

	private String action;

	/**
	 * @return the projectIdToUpdate
	 */
	public String getProjectIdToUpdate() {
		return projectIdToUpdate;
	}

	/**
	 * @param projectIdToUpdate the projectIdToUpdate to set
	 */
	public void setProjectIdToUpdate(String projectIdToUpdate) {
		this.projectIdToUpdate = projectIdToUpdate;
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

	/**
	 * @return
	 */
	public String getAction() {
		return action;
	}

	/**
	 * @param action
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * @return
	 */
	public String getUrl() {
		return "/editProject?projectIdToUpdate=" + this.projectIdToUpdate;
	}
}
