package org.bibsonomy.webapp.validation;

import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.actions.UserRegistrationCommand;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 * @author rja
 * @version $Id$
 */
public class UserRegistrationValidator implements Validator<UserRegistrationCommand> {

	@SuppressWarnings("unchecked")
	public boolean supports(final Class clazz) {
		return UserRegistrationCommand.class.equals(clazz);
	}

	/**
	 * Validates the given userObj.
	 * 
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
	public void validate(final Object userObj, final Errors errors) {

		/*
		 * To ensure that the received command is not null, we throw an
		 * exception, if this assertion fails.
		 */
		Assert.notNull(userObj);
		
		final UserRegistrationCommand command = (UserRegistrationCommand) userObj;
		final User user = command.getRegisterUser();
		
		/*
		 * validate user
		 */
		errors.pushNestedPath("registerUser");
		ValidationUtils.invokeValidator(new UserValidator(), user, errors);
		errors.popNestedPath();
	}

}
