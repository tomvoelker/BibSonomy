package org.bibsonomy.webapp.controller.cris;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.query.ProjectQuery;
import org.bibsonomy.model.statistics.Statistics;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.cris.ProjectsPageCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

import java.util.List;

/**
 * controller for displaying a list of projects
 * paths:
 *    - /projects
 *
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
		final ListCommand<Project> projectListCommand = command.getProjects();

		// build the query based on the commands
		final ProjectQuery.ProjectQueryBuilder builder = ProjectQuery.createBuilder();
		final int start = projectListCommand.getStart();
		builder.projectStatus(command.getProjectStatus())
						.start(start)
						.end(start + projectListCommand.getEntriesPerPage())
						.search(command.getSearch())
						.prefix(command.getPrefix())
						.order(command.getProjectOrder())
						.sortOrder(command.getSortOrder());

		// query the logic for matching projects
		final ProjectQuery projectQuery = builder.build();
		final List<Project> projects = this.logic.getProjects(projectQuery);
		projectListCommand.setList(projects);

		if (!present(projectListCommand.getTotalCountAsInteger())) {
			final Statistics stats = this.logic.getStatistics(projectQuery);
			projectListCommand.setTotalCount(stats.getCount());
		}

		return Views.PROJECT_PAGE;
	}

	/**
	 * @param logic the logic to set
	 */
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}
}
