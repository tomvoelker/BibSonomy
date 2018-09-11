package org.bibsonomy.webapp.controller.actions;

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

		updateProject(command.getProject(), command);
		final String referer = command.getReferer();
		if (present(referer)) {
			return new ExtendedRedirectView(referer);
		}
		return new ExtendedRedirectView(this.urlGenerator.getProjectHome());
	}

	/**
	 *
	 * @param project
	 * @param command
	 */
	private void updateProject(Project project, EditProjectCommand command) {
		project.setTitle(command.getTitle());
		project.setSubTitle(command.getSubTitle());
		project.setDescription(command.getDescription());
		project.setType(command.getType());
		project.setBudget(command.getBudget());
		project.setStartDate(command.getStartDate());
		project.setEndDate(command.getEndDate());
		project.setParentProject(command.getParentProject());
		project.setSubProjects(command.getSubProjects());
		project.setCrisLinks(command.getCrisLinks());

		// todo catch errors
		// this.logic.updateProject(project.getExternalId(), project);
	}

	/**
	 *
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
