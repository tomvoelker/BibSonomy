package org.bibsonomy.webapp.controller;

import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.enums.ProjectStatus;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.query.ProjectQuery;
import org.bibsonomy.webapp.command.ProjectsPageCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

import java.util.List;

/**
 * controller for displaying a list of projects
 * @author dzo
 */
public class ProjectsPageController implements MinimalisticController<ProjectsPageCommand> {

	private LogicInterface logic;

	@Override
	public ProjectsPageCommand instantiateCommand() {
		return new ProjectsPageCommand();
	}

	@Override
	public View workOn(final ProjectsPageCommand command) {
		final List<Project> projects = this.logic.getProjects(ProjectQuery.createBuilder().projectStatus(ProjectStatus.RUNNING).build());
		command.setProjects(projects);

		return Views.PROJECT_PAGE;
	}

	/**
	 * @param logic the logic to set
	 */
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}
}
