package org.bibsonomy.webapp.command.actions;

import org.bibsonomy.model.cris.Project;
import org.bibsonomy.webapp.command.BaseCommand;


public class EditProjectCommand extends BaseCommand {

	private String projectIdToUpdate;

	private Project project;

	private String save;

	private String delete;

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
	public String getSave() {
		return save;
	}

	/**
	 * @param save
	 */
	public void setSave(String save) {
		this.save = save;
	}

	/**
	 * @return
	 */
	public String getDelete() {
		return delete;
	}

	/**
	 * @param delete
	 */
	public void setDelete(String delete) {
		this.delete = delete;
	}

	/**
	 * @return
	 */
	public String getUrl() {
		return "/editProject?projectIdToUpdate=" + this.projectIdToUpdate;
	}
}
