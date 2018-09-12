package org.bibsonomy.webapp.controller.actions;

import org.bibsonomy.common.JobResult;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.common.exceptions.ObjectNotFoundException;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.util.UrlUtils;
import org.bibsonomy.webapp.command.actions.EditProjectCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;

import static org.bibsonomy.util.ValidationUtils.present;

/**
 * controller for editing and creating a project
 */
public class EditProjectController implements MinimalisticController<EditProjectCommand> {
	private LogicInterface logic;
	private URLGenerator urlGenerator;
	protected RequestLogic requestLogic;

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
			return returnEditView(requestedProjectId, command);
		}

		JobResult result = this.updateProject(command.getProject());
		final String referer = command.getReferer();
		if (present(referer)) {
			// todo never has referer
			return new ExtendedRedirectView(referer);
		}
		return new ExtendedRedirectView(this.urlGenerator.getProjectsUrl());
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
			throw new ObjectNotFoundException("project with id '" + requestedProjectId + "' not found");
		}
		command.setProject(projectDetails);

		/*
		 * We store the referrer in the command, to send the user back to the
		 * page he's coming from at the end of the posting process.
		 */
		if (!present(command.getReferer())) {
			String referer = this.requestLogic.getReferer();
			if (referer == null) {
				final String url = command.getUrl();
				if (UrlUtils.isHTTPS(url)) {
					referer = url;
				} else {
					referer = null;
				}
			}
			command.setReferer(referer);
		}

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

	public void setRequestLogic(RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}
}
