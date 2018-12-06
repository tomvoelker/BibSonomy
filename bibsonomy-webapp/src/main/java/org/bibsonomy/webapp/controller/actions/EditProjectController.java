package org.bibsonomy.webapp.controller.actions;

import static org.bibsonomy.util.ValidationUtils.present;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.JobResult;
import org.bibsonomy.common.enums.Status;
import org.bibsonomy.common.errors.ErrorMessage;
import org.bibsonomy.common.errors.MissingFieldErrorMessage;
import org.bibsonomy.common.errors.MissingObjectErrorMessage;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.webapp.command.actions.EditProjectCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectViewWithAttributes;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * controller for editing and creating a project
 *
 * handles requests for
 *
 * - /editProject
 *
 * @author tko, dzo
 */
public class EditProjectController implements MinimalisticController<EditProjectCommand>, ErrorAware {
	private static final Log LOG = LogFactory.getLog(EditProjectController.class);

	private LogicInterface logic;
	private URLGenerator urlGenerator;
	private Errors errors;

	@Override
	public EditProjectCommand instantiateCommand() {
		final EditProjectCommand command = new EditProjectCommand();
		final Project project = new Project();
		project.setParentProject(new Project());
		command.setProject(project);
		return command;
	}

	@Override
	public View workOn(final EditProjectCommand command) {
		final RequestWrapperContext context = command.getContext();
		final String requestedProjectId = command.getProjectIdToUpdate();

		// was the edit form called for the first time?
		final boolean firstCall = context.isFirstCall();

		// TODO: restirct access to admins?
		if (!context.isUserLoggedIn()) {
			throw new AccessDeniedException("please log in");
		}

		if (!context.isValidCkey() && !firstCall) {
			this.errors.reject("error.field.valid.ckey", "The provided security token is invalid.");
			return Views.ERROR;
		}

		if (present(requestedProjectId)) {
			// edit the specified project
			final Project originalProject = this.logic.getProjectDetails(requestedProjectId);
			if (!present(originalProject)) {
				this.errors.reject("error.project.notfound", "The project to edit could not be found");
				return Views.ERROR;
			}

			// check for first visit
			if (firstCall) {
				// only set the project and
				command.setProject(originalProject);
				return Views.EDIT_PROJECT;
			}

			final Project project = command.getProject();

			// load the parent project by parent id
			final Project parentProject = this.logic.getProjectDetails(project.getParentProject().getExternalId());
			project.setParentProject(parentProject);

			final JobResult result = this.logic.updateProject(requestedProjectId, project);
			// add the job error to the errors object
			this.handleErrors(result);

			if (this.errors.hasErrors()) {
				return Views.EDIT_PROJECT;
			}

			final ExtendedRedirectViewWithAttributes redirect = new ExtendedRedirectViewWithAttributes(this.urlGenerator.getProjectUrlByProject(project));
			redirect.addAttribute(ExtendedRedirectViewWithAttributes.SUCCESS_MESSAGE_KEY, "project.edit.success");
			return redirect;
		}

		// handle create post
		if (!firstCall) {
			final Project project = command.getProject();

			// reset the parent if no one is specified
			final Project parentProject = project.getParentProject();
			final String externalId = parentProject.getExternalId();
			if (!present(externalId)) {
				project.setParentProject(null);
			}

			final JobResult result = this.logic.createProject(project);
			final String projectExternalId = result.getId();
			// add job errors the errors object of the controller
			this.handleErrors(result);

			if (this.errors.hasErrors()) {
				return Views.EDIT_PROJECT;
			}

			// new project created, redirect to the new project page
			final ExtendedRedirectViewWithAttributes redirect = new ExtendedRedirectViewWithAttributes(this.urlGenerator.getProjectUrlByProjectId(projectExternalId));
			redirect.addAttribute(ExtendedRedirectViewWithAttributes.SUCCESS_MESSAGE_KEY, "project.create.success");
			return redirect;
		}

		return Views.EDIT_PROJECT;
	}

	// FIXME: move this to a utils class!
	private void handleErrors(JobResult result) {
		if (Status.OK.equals(result.getStatus())) {
			// everything is fine; no errors; action finished without error
			return;
		}

		for (final ErrorMessage e : result.getErrors()) {
			if (e instanceof MissingFieldErrorMessage) {
				final MissingFieldErrorMessage missingFieldErrorMessage = (MissingFieldErrorMessage) e;
				final String missingField = missingFieldErrorMessage.getMissing();
				this.errors.rejectValue("project." + missingField, e.getErrorCode(), e.getDefaultMessage());
			} else if (e instanceof MissingObjectErrorMessage) {
				final MissingObjectErrorMessage missingObjectErrorMessage = (MissingObjectErrorMessage) e;
				this.errors.rejectValue("project.parentProject.externalID", missingObjectErrorMessage.getErrorCode(), missingObjectErrorMessage.getDefaultMessage());
			} else {
				LOG.error("error message not handled by this error message conversion" + e.getClass());
			}
		}
	}

	/**
	 * @param logic the logic to set
	 */
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}

	/**
	 * @param urlGenerator
	 */
	public void setUrlGenerator(URLGenerator urlGenerator) {
		this.urlGenerator = urlGenerator;
	}

	@Override
	public Errors getErrors() {
		return this.errors;
	}

	@Override
	public void setErrors(Errors errors) {
		this.errors = errors;
	}
}
