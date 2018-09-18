package org.bibsonomy.webapp.controller.actions;

import org.bibsonomy.common.JobResult;
import org.bibsonomy.common.errors.ErrorMessage;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.webapp.command.actions.CreateNewProjectCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectViewWithAttributes;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

public class CreateNewProjectController implements MinimalisticController<CreateNewProjectCommand>, ErrorAware {
	private Errors errors;
	private LogicInterface logic;
	private URLGenerator urlGenerator;

	@Override
	public CreateNewProjectCommand instantiateCommand() {
		return new CreateNewProjectCommand();
	}

	@Override
	public View workOn(CreateNewProjectCommand command) {
		final RequestWrapperContext context = command.getContext();

		if (!context.isUserLoggedIn()) {
			throw new AccessDeniedException("please log in");
		}

		if (!context.isValidCkey()) {
			this.errors.reject("error.field.valid.ckey", "The provided security token is invalid.");
			return Views.NEW_PROJECT;
		}
		Project project = command.getProject();
		JobResult result = this.logic.createProject(project);
		if (!result.getStatus().getMessage().equals("OK")) {
			for (ErrorMessage e : result.getErrors()) {
				String errorMessage = e.getErrorCode();
				String error = errorMessage.split("\\.")[errorMessage.split("\\.").length - 1];
				if (error.contains("date")) {
					error = error.replace("date", "Date");
				}
				int lastIndex = errorMessage.lastIndexOf("\\.");
				String prefix = errorMessage.substring(0, lastIndex);
				this.errors.rejectValue("project." + error, prefix + error, e.getDefaultMessage());
				// this.errors.rejectValue("project." + ((MissingFieldErrorMessage)e).getMissing(), e.getErrorCode(), e.getDefaultMessage());
			}
		}
		if (this.errors.hasErrors()) {
			return Views.NEW_PROJECT;
		}
		final ExtendedRedirectViewWithAttributes redirect = new ExtendedRedirectViewWithAttributes(this.urlGenerator.getProjectUrlByProject(command.getProject()));
		redirect.addAttribute(ExtendedRedirectViewWithAttributes.SUCCESS_MESSAGE_KEY, "project.edit.success");
		return redirect;
	}

	@Override
	public Errors getErrors() {
		return errors;
	}

	@Override
	public void setErrors(Errors errors) {
		this.errors = errors;
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
