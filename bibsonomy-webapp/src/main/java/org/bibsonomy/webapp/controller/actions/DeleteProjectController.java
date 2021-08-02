package org.bibsonomy.webapp.controller.actions;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.webapp.command.actions.DeleteProjectCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;

/**
 * the controler deletes projects
 *
 * request paths:
 *
 * - /deleteProject
 *
 * @author tko
 */
public class DeleteProjectController implements MinimalisticController<DeleteProjectCommand> {
	private LogicInterface logic;
	private URLGenerator urlGenerator;

	@Override
	public DeleteProjectCommand instantiateCommand() {
		return new DeleteProjectCommand();
	}

	@Override
	public View workOn(final DeleteProjectCommand command) {
		final RequestWrapperContext context = command.getContext();
		final String requestedProjectId = command.getProjectIdToDelete();

		final Project project = this.logic.getProjectDetails(requestedProjectId);
		if (!present(project)) {
			return new ExtendedRedirectView(this.urlGenerator.getProjectsUrl());
		}

		// check if the project can be deleted
		// FIXME: add check that the user is manager of the project
		if (!(context.getLoginUser().getRole() == Role.ADMIN)) {
			throw new AccessDeniedException("not allowed");
		}

		// delete the project and redirect
		this.logic.deleteProject(requestedProjectId);
		// TODO: add success message
		return new ExtendedRedirectView(this.urlGenerator.getProjectsUrl());
	}

	/**
	 * @param urlGenerator the urlGenerator to set
	 */
	public void setUrlGenerator(final URLGenerator urlGenerator) {
		this.urlGenerator = urlGenerator;
	}

	/**
	 * @param logic the logic to set
	 */
	public void setLogic(final LogicInterface logic) {
		this.logic = logic;
	}
}
