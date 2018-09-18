package org.bibsonomy.webapp.command.actions;

import org.bibsonomy.model.cris.Project;
import org.bibsonomy.webapp.command.BaseCommand;

public class CreateNewProjectCommand extends BaseCommand {

	private Project project;

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
