package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.common.exceptions.ObjectNotFoundException;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.ProjectPageCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * controller for a single project
 *  - /project/PROJECT_ID
 *
 * @author dzo
 */
public class ProjectPageController implements MinimalisticController<ProjectPageCommand> {
	private LogicInterface logic;

	@Override
	public ProjectPageCommand instantiateCommand() {
		return new ProjectPageCommand();
	}

	@Override
	public View workOn(final ProjectPageCommand command) {
		final String requestedProjectId = command.getRequestedProjectId();
		if (!present(requestedProjectId)) {
			throw new MalformedURLSchemeException("error.project_without_project_id");
		}

		/*
		 * get the project details form the logic
		 */
		final Project projectDetails = this.logic.getProjectDetails(requestedProjectId);
		if (!present(projectDetails)) {
			throw new ObjectNotFoundException("project with id '" + requestedProjectId + "' not found");
		}
		command.setProject(projectDetails);

		return Views.PROJECT_DETAILS_PAGE;
	}

	/**
	 * @param logic the logic to set
	 */
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}
}
