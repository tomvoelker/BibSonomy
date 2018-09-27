package org.bibsonomy.webapp.controller;

import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.webapp.command.DeletePublicationFromPersonCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;

public class DeletePublicationFromPersonController implements MinimalisticController<DeletePublicationFromPersonCommand> {
	private LogicInterface logic;
	private URLGenerator urlGenerator;

	@Override
	public DeletePublicationFromPersonCommand instantiateCommand() {
		return new DeletePublicationFromPersonCommand();
	}

	@Override
	public View workOn(DeletePublicationFromPersonCommand command) {
		// todo delete Publication Relation
		return new ExtendedRedirectView(this.urlGenerator.getPersonUrl(command.getPerson().getPersonId()));
	}

	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}

	public LogicInterface getLogic() {
		return logic;
	}

	public URLGenerator getUrlGenerator() {
		return urlGenerator;
	}

	public void setUrlGenerator(URLGenerator urlGenerator) {
		this.urlGenerator = urlGenerator;
	}
}
