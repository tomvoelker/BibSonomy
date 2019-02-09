package org.bibsonomy.webapp.controller.reporting;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.model.Group;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.enums.PersonIdType;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.query.ProjectQuery;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.reporting.ProjectReportingCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * @author pda
 */
public class ProjectReportingPageController implements MinimalisticController<ProjectReportingCommand> {

	private LogicInterface logic;

	@Override
	public ProjectReportingCommand instantiateCommand() {
		final ProjectReportingCommand projectReportingCommand = new ProjectReportingCommand();
		projectReportingCommand.setPerson(new Person());
		projectReportingCommand.setOrganization(new Group());
		return projectReportingCommand;
	}

	@Override
	public View workOn(ProjectReportingCommand command) {
		final ListCommand<Project> projectsPageCommand = command.getProjects();
		final int start = projectsPageCommand.getStart();
		final Person person = command.getPerson();
		final ProjectQuery projectQuery = ProjectQuery.createBuilder().search(command.getSearch()).
						startDate(command.getStartDate())
						.endDate(command.getEndDate()).type(command.getType()).
						sponsor(command.getSponsor()).prefix(command.getPrefix()).
						start(start).
						end(start + projectsPageCommand.getEntriesPerPage()).
						person(person).organization(command.getOrganization())
						.build();
		final List<Project> projects = this.logic.getProjects(projectQuery);
		projectsPageCommand.setList(projects);

		if (present(command.getDownloadFormat())) {
			return Views.REPORTING_DOWNLOAD;
		}

		final String personId = person.getPersonId();
		if (present(personId)) {
			final Person personById = this.logic.getPersonById(PersonIdType.PERSON_ID, personId);
			command.setPerson(personById);
		}

		return Views.PROJECTS_REPORTING;
	}

	/**
	 * @param logic the logic to set
	 */
	public void setLogic(final LogicInterface logic) {
		this.logic = logic;
	}
}
