package org.bibsonomy.webapp.controller.actions;

import org.bibsonomy.common.JobResult;
import org.bibsonomy.common.errors.ErrorMessage;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.webapp.command.actions.EditProjectCommand;
import org.bibsonomy.webapp.util.*;
import org.bibsonomy.webapp.view.ExtendedRedirectViewWithAttributes;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

import static org.bibsonomy.util.ValidationUtils.present;

/**
 * controller for editing and creating a project
 */
public class EditProjectController implements MinimalisticController<EditProjectCommand>, ErrorAware {
	private LogicInterface logic;
	private URLGenerator urlGenerator;
	private Errors errors;

	@Override
	public EditProjectCommand instantiateCommand() {
		return new EditProjectCommand();
	}

	@Override
	public View workOn(final EditProjectCommand command) {
		final RequestWrapperContext context = command.getContext();
		final String requestedProjectId = command.getProjectIdToUpdate();

		if (!context.isUserLoggedIn()) {
			throw new AccessDeniedException("please log in");
		}

		if (!context.isValidCkey()) {
			this.errors.reject("error.field.valid.ckey", "The provided security token is invalid.");
			return returnEditView(requestedProjectId, command);
		}

		command.setProjectIdToUpdate(command.getProject().getExternalId());

		JobResult result = this.updateProject(command.getProject());
		if (!result.getStatus().getMessage().equals("OK")) {
			for (ErrorMessage e : result.getErrors()) {
				String error = e.getErrorCode().split("\\.")[e.getErrorCode().split("\\.").length - 1];
				this.errors.rejectValue("project." + error, e.getErrorCode(), e.getDefaultMessage());
			}
		}
		if (this.errors.hasErrors()) {
			return Views.EDIT_PROJECT;
		}
		final ExtendedRedirectViewWithAttributes redirect = new ExtendedRedirectViewWithAttributes(this.urlGenerator.getProjectUrlByProject(command.getProject()));
		redirect.addAttribute(ExtendedRedirectViewWithAttributes.SUCCESS_MESSAGE_KEY, "project.edit.success");
		return redirect;
	}

	/**
	 * Set the properties not editable in this view.
	 * @param project
	 */
	private JobResult updateProject(Project project) {
		Project originalProject = this.logic.getProjectDetails(project.getExternalId());
		project.setSubProjects(originalProject.getSubProjects());
		project.setCrisLinks(originalProject.getCrisLinks());
		project.setId(originalProject.getId());
		Project parentProject = this.logic.getProjectDetails(project.getParentProject().getExternalId());
		project.setParentProject(parentProject);
		return this.logic.updateProject(project.getExternalId(), project);
	}

	/**
	 * @param requestedProjectId
	 * @param command
	 * @return
	 */
	private View returnEditView(String requestedProjectId, EditProjectCommand command){
		final Project projectDetails = this.logic.getProjectDetails(requestedProjectId);
		if (!present(projectDetails)) {
			this.errors.reject("error.project.not.found");
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
