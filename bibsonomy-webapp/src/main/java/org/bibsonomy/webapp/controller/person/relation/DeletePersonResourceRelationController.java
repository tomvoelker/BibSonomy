package org.bibsonomy.webapp.controller.person.relation;

import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.webapp.command.person.relation.PersonResourceRelationCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;

/**
 * TODO: add documentetion to this controller
 *
 * @author tok
 */
public class DeletePersonResourceRelationController implements MinimalisticController<PersonResourceRelationCommand> {
	private LogicInterface logic;
	private URLGenerator urlGenerator;

	@Override
	public PersonResourceRelationCommand instantiateCommand() {
		return new PersonResourceRelationCommand();
	}

	@Override
	public View workOn(final PersonResourceRelationCommand command) {
		final String personId = command.getPerson().getPersonId();
		final PersonResourceRelationType typeToDelete = command.getType();
		final String interhashToDelete = command.getInterhash();
		final int indexToDelete = command.getIndex();

		// this.logic.removeResourceRelation(interhashToDelete, indexToDelete, typeToDelete);
		return new ExtendedRedirectView(this.urlGenerator.getPersonUrl(personId));
	}

	/**
	 * @param logic the logic to set
	 */
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}

	/**
	 * @param urlGenerator the urlGenerator to set
	 */
	public void setUrlGenerator(URLGenerator urlGenerator) {
		this.urlGenerator = urlGenerator;
	}
}
