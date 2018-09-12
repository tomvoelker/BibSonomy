package org.bibsonomy.webapp.command.actions;

import org.bibsonomy.model.cris.Project;
import org.bibsonomy.webapp.command.BaseCommand;

import java.net.URL;
import java.util.Date;

public class EditProjectCommand extends BaseCommand {

	private String projectIdToUpdate;

	private Project project;

	/**
	 * For some pages we need to store the referer to send the user back
	 * to that page.
	 */
	private String referer;

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
	public String getReferer() {
		return this.referer;
	}

	/**
	 * @param referer
	 */
	public void setReferer(String referer) {
		this.referer = referer;
	}

	/**
	 *
	 * @return
	 */
	public String getUrl() {
		return "/editProject?projectIdToUpdate=" + this.projectIdToUpdate;
	}
}
