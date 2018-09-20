package org.bibsonomy.webapp.controller.actions;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.model.User;
import org.bibsonomy.model.cris.CRISLink;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.webapp.command.actions.DeleteProjectCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;


public class DeleteProjectController implements MinimalisticController<DeleteProjectCommand> {
	private LogicInterface logic;
	private URLGenerator urlGenerator;

	@Override
	public DeleteProjectCommand instantiateCommand() {
		return new DeleteProjectCommand();
	}

	@Override
	public View workOn(DeleteProjectCommand command) {
		final RequestWrapperContext context = command.getContext();
		final String requestedProjectId = command.getProjectIdToDelete();

		Project project = this.logic.getProjectDetails(requestedProjectId);
		if (!present(project)) {
			return new ExtendedRedirectView(this.urlGenerator.getProjectsUrl());
		}

		// --- Check if allowed ---
		boolean isMember = false;
		for (CRISLink link : project.getCrisLinks()) {
			User user;
			try {
				user = (User) link.getTarget();
			} catch (Exception e) {
				user = null;
			}
			// fixme cris links return person, not user
			if (present(user) && user.equals(command.getContext().getLoginUser())) {
				isMember = true;
			}
		}
		boolean a = !(context.getLoginUser().getRole().getRole() == 0);
		boolean b = !(context.getLoginUser().getRole().getRole() == 0) || isMember;
		boolean c = !(context.getLoginUser().getRole().getRole() == 0) && isMember;
		if (!(context.getLoginUser().getRole().getRole() == 0) || isMember) {
			throw new AccessDeniedException("not allowed");
		}

		// --- delete and redirect ---
		logic.deleteProject(requestedProjectId);
		return new ExtendedRedirectView(this.urlGenerator.getProjectsUrl());
	}

	/**
	 * @param urlGenerator
	 */
	public void setUrlGenerator(URLGenerator urlGenerator) {
		this.urlGenerator = urlGenerator;
	}

	/**
	 * @return
	 */
	public URLGenerator getUrlGenerator() {
		return urlGenerator;
	}

	/**
	 * @return
	 */
	public LogicInterface getLogic() {
		return logic;
	}

	/**
	 * @param logic
	 */
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}
}
