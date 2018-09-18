package org.bibsonomy.webapp.controller.actions;

import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.webapp.view.ExtendedRedirectViewWithAttributes;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.webapp.command.actions.EditProjectMemberCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;

import static org.bibsonomy.util.ValidationUtils.present;

public class EditProjectMemberController implements MinimalisticController<EditProjectMemberCommand>, ErrorAware {

	private Errors errors;
	private URLGenerator urlGenerator;
	private LogicInterface logic;

	@Override
	public EditProjectMemberCommand instantiateCommand() {
		return new EditProjectMemberCommand();
	}

	@Override
	public View workOn(EditProjectMemberCommand command) {
		final RequestWrapperContext context = command.getContext();
		final String requestedProjectId = command.getProjectIdToUpdate();
		return Views.PROJECT_PAGE;
	}

	@Override
	public void setErrors(org.springframework.validation.Errors errors) {
		this.errors = errors;
	}

	@Override
	public Errors getErrors() {
		return errors;
	}

	/**
	 * @param logic
	 */
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}

	/**
	 * @return
	 */
	public LogicInterface getLogic() {
		return logic;
	}

	/**
	 * @return
	 */
	public URLGenerator getUrlGenerator() {
		return urlGenerator;
	}

	/**
	 * @param urlGenerator
	 */
	public void setUrlGenerator(URLGenerator urlGenerator) {
		this.urlGenerator = urlGenerator;
	}
}
