package org.bibsonomy.webapp.validation;

import org.bibsonomy.webapp.command.actions.UserLoginCommand;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 * @author rja
 * @version $Id$
 */
public class UserLoginValidator implements Validator<UserLoginCommand> {

	@SuppressWarnings("unchecked")
	public boolean supports(final Class clazz) {
		return UserLoginCommand.class.equals(clazz);
	}

	/**
	 * Validates the given loginObj.
	 * 
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
	public void validate(final Object loginObj, final Errors errors) {
		/*
		 * To ensure that the received command is not null, we throw an
		 * exception, if this assertion fails.
		 */
		Assert.notNull(loginObj);
		
		UserLoginCommand cmd = (UserLoginCommand) loginObj;

		if (!org.bibsonomy.util.ValidationUtils.present(cmd.getOpenID()) && 
			!org.bibsonomy.util.ValidationUtils.present(cmd.getUsername()) &&
			!org.bibsonomy.util.ValidationUtils.present(cmd.getPassword())) {
			
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "error.field.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "error.field.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "openID", "error.field.required");
		}		
	}
}