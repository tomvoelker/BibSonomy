package org.bibsonomy.webapp.controller.person.relation;

import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.webapp.command.person.relation.PersonResourceRelationCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectViewWithAttributes;
import org.springframework.validation.Errors;

/**
 * TODO: add documentetion to this controller
 *
 * @author tok
 */
public class DeletePersonResourceRelationController implements MinimalisticController<PersonResourceRelationCommand>, ErrorAware {
	private LogicInterface logic;
	private URLGenerator urlGenerator;
	private Errors errors;

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

		try {
			this.logic.removeResourceRelation(interhashToDelete, indexToDelete, typeToDelete);
		} catch (Exception e) {
			errors.reject("person.error.addRelation");
		}
		final ExtendedRedirectViewWithAttributes redirect = new ExtendedRedirectViewWithAttributes(this.urlGenerator.getPersonUrl(personId));
		if (this.errors.hasErrors()) {
			redirect.addAttribute(ExtendedRedirectViewWithAttributes.ERRORS_KEY, this.errors);
		} else {
			redirect.addAttribute(ExtendedRedirectViewWithAttributes.SUCCESS_MESSAGE_KEY, "person.success.addRelation");
		}
		return redirect;
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

	@Override
	public Errors getErrors() {
		return this.errors;
	}

	@Override
	public void setErrors(Errors errors) {
		this.errors = errors;
	}
}
