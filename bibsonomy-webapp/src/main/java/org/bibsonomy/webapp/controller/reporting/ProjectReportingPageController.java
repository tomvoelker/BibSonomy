package org.bibsonomy.webapp.controller.reporting;

import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.query.ProjectQuery;
import org.bibsonomy.webapp.command.reporting.ProjectReportingCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

public class ProjectReportingPageController implements MinimalisticController<ProjectReportingCommand> {

	private LogicInterface logic;

	/**
	 * @param logic the logic to set
	 */
	public void setLogic(final LogicInterface logic) {
		this.logic = logic;
	}

	@Override
	public ProjectReportingCommand instantiateCommand() {
		return new ProjectReportingCommand();
	}

	@Override
	public View workOn(ProjectReportingCommand command) {
		final ProjectQuery projectQuery = ProjectQuery.createBuilder().search(command.getSearch()).
						startDate(command.getStartDate()).endDate(command.getEndDate()).type(command.getType()).build();
		command.setProjects(logic.getProjects(projectQuery));
		return Views.PROJECTS_REPORTING;
	}
}
