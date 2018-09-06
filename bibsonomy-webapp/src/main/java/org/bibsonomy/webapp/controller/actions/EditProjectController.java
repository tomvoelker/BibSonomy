package org.bibsonomy.webapp.controller.actions;

import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.common.exceptions.ObjectNotFoundException;
import org.bibsonomy.model.User;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.enums.ProjectStatus;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.query.ProjectQuery;
import org.bibsonomy.webapp.command.actions.EditProjectCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

import java.util.List;

import static org.bibsonomy.util.ValidationUtils.present;

/**
 * controller for editing and creating a project
 */
public class EditProjectController implements MinimalisticController<EditProjectCommand> {
	private LogicInterface logic;

	@Override
	public EditProjectCommand instantiateCommand() {
		return new EditProjectCommand();
	}

	@Override
	public View workOn(final EditProjectCommand command) {
		final RequestWrapperContext context = command.getContext();
		final String requestedProjectId = command.getProjectIdToUpdate();
		final User loginUser = context.getLoginUser();

		/**if (!context.isUserLoggedIn() || Role.ADMIN.equals(loginUser.getRole())) {
			throw new AccessDeniedException("please log in");
		}*/

		final Project projectDetails = this.logic.getProjectDetails(requestedProjectId);
		if (!present(projectDetails)) {
			throw new ObjectNotFoundException("project with id '" + requestedProjectId + "' not found");
		}
		command.setProject(projectDetails);


		return Views.EDIT_PROJECT;
	}

	/**
	 * @param logic the logic to set
	 */
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}
}
