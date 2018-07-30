package org.bibsonomy.webapp.command;

import org.bibsonomy.model.cris.Project;

import java.util.List;

public class ProjectsPageCommand extends BaseCommand {

	private List<Project> projects;

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
