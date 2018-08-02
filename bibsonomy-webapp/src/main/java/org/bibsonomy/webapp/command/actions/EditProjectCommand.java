package org.bibsonomy.webapp.command.actions;

import org.bibsonomy.model.cris.Project;
import org.bibsonomy.webapp.command.BaseCommand;

import java.util.List;

public class EditProjectCommand extends BaseCommand {

	private String projectIdToUpdate;

	private Project project;

	private List<Project> projects;

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
	 * @return the projects
	 */
	public List<Project> getProjects() {
		return projects;
	}

	/**
	 * @param projects the projects to set
	 */
	public void setProjects(List<Project> projects) {
		this.projects = projects;
	}
}
