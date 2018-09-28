package org.bibsonomy.webapp.controller;

import org.bibsonomy.model.enums.PersonResourceRelationType;
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
		final String personId = command.getPerson().getPersonId();
		final String typeToDelete = command.getTypeToDelete();
		final String interhashToDelete = command.getInterhashToDelete();
		final String indexToDelete = command.getIndexToDelete();
		int intIndexToDelete;
		PersonResourceRelationType enumToDelete;
		try {
			intIndexToDelete = Integer.parseInt(indexToDelete);
			enumToDelete = PersonResourceRelationType.valueOf(typeToDelete);
		} catch (Exception e) {
			// todo add errors
			return new ExtendedRedirectView(this.urlGenerator.getPersonUrl(personId));
		}

		this.logic.removeResourceRelation(interhashToDelete, intIndexToDelete, enumToDelete);
		return new ExtendedRedirectView(this.urlGenerator.getPersonUrl(personId));
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
