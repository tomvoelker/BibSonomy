package org.bibsonomy.webapp.controller.cris.ajax;

import org.bibsonomy.model.Person;
import org.bibsonomy.model.cris.CRISLink;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.webapp.command.cris.ajax.ProjectPersonLinkAjaxCommand;
import org.bibsonomy.webapp.controller.ajax.AjaxController;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * Controller for linking projects and persons
 * FIXME: add more error handling
 *
 * @author dzo
 */
public class ProjectPersonLinkAjaxController extends AjaxController implements MinimalisticController<ProjectPersonLinkAjaxCommand> {

	@Override
	public ProjectPersonLinkAjaxCommand instantiateCommand() {
		return new ProjectPersonLinkAjaxCommand();
	}

	@Override
	public View workOn(final ProjectPersonLinkAjaxCommand command) {
		final RequestWrapperContext context = command.getContext();
		if (!context.isUserLoggedIn()) {
			return this.getErrorView();
		}

		if (!context.isValidCkey()) {
			return this.getErrorView();
		}

		final CRISLink link = new CRISLink();
		link.setLinkType(command.getLinkType());
		final Project project = new Project();
		project.setExternalId(command.getProjectId());
		link.setSource(project);
		final Person person = new Person();
		person.setPersonId(command.getPersonId());
		link.setTarget(person);

		// FIXME: check jobresult
		this.logic.createCRISLink(link);

		command.setResponseString("{}");
		return Views.AJAX_JSON;
	}
}
