package org.bibsonomy.webapp.validation;

import org.bibsonomy.webapp.command.actions.UserSamlActivationCommand;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

/**
 * @author nilsraabe
 * @version $Id$
 */
public class UserSamlActivationValidation implements Validator<UserSamlActivationCommand>{

	@Override
	public boolean supports(Class<?> arg0) {
		return UserSamlActivationCommand.class.equals(arg0);
	}

	@Override
	public void validate(Object userObj, Errors errors) {
		
		/**
		 * To ensure that the received command is not null, we throw an
		 * exception, if this assertion fails.
		 */
		Assert.notNull(userObj);
		final UserSamlActivationCommand command = (UserSamlActivationCommand) userObj;
		
		/**
		 * check if the user accepts our privacy statement about SAML
		 */
		if (!command.isCheckboxAccept()) {
			errors.rejectValue("checkboxAccept", "saml.error.checkbox");
		}

	}

}
