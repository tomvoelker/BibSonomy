package org.bibsonomy.webapp.validation;

import org.bibsonomy.webapp.command.actions.LimitedAccountActivationCommand;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

/**
 * @author nilsraabe
 * @version $Id$
 */
public class LimitedAccountActivationValidation implements Validator<LimitedAccountActivationCommand>{

	@Override
	public boolean supports(final Class<?> clazz) {
		return LimitedAccountActivationCommand.class.equals(clazz);
	}

	@Override
	public void validate(Object userObj, Errors errors) {
		
		/**
		 * To ensure that the received command is not null, we throw an
		 * exception, if this assertion fails.
		 */
		Assert.notNull(userObj);
		final LimitedAccountActivationCommand command = (LimitedAccountActivationCommand) userObj;
		
		/**
		 * check if the user accepts our privacy statement about SAML
		 */
		if (!command.isCheckboxAccept()) {
			errors.rejectValue("checkboxAccept", "limited_account.activation.error.checkbox");
		}
		
		errors.pushNestedPath("registerUser");
		UserValidator.validateUser(command.getRegisterUser(), errors);
		errors.popNestedPath();
	}

}
