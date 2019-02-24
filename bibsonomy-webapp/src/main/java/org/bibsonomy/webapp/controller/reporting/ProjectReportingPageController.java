package org.bibsonomy.webapp.controller.reporting;

import java.util.List;

import org.bibsonomy.model.Group;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.logic.query.ProjectQuery;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.reporting.ProjectReportingCommand;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * controller for the project reporting page
 * - /reporting/projects
 *
 * @author pda
 * @author dzo
 */
public class ProjectReportingPageController extends AbstractReportingPageController<ProjectReportingCommand> {

	@Override
	protected ProjectReportingCommand instantiateReportingCommand() {
		return new ProjectReportingCommand();
	}

	@Override
	protected void workOn(ProjectReportingCommand command, Person person, Group organization) {
		final ListCommand<Project> projectsPageCommand = command.getProjects();
		final int start = projectsPageCommand.getStart();
		final ProjectQuery projectQuery = ProjectQuery.createBuilder().search(command.getSearch()).
						startDate(command.getStartDate())
						.endDate(command.getEndDate()).type(command.getType()).
										sponsor(command.getSponsor()).prefix(command.getPrefix()).
										start(start).
										end(start + projectsPageCommand.getEntriesPerPage()).
										person(person).organization(organization)
						.build();
		final List<Project> projects = this.logic.getProjects(projectQuery);
		projectsPageCommand.setList(projects);
	}

	@Override
	protected View reportingView() {
		return Views.PROJECTS_REPORTING;
	}
}
