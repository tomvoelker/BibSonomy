package org.bibsonomy.webapp.validation;

import org.bibsonomy.webapp.command.actions.PasswordReminderCommand;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 * @author daill
 * @version $Id$
 */
public class PasswordReminderValidator implements Validator<PasswordReminderCommand>{

	@Override
	public boolean supports(final Class<?> clazz) {
		return PasswordReminderCommand.class.equals(clazz);
	}

	@Override
	public void validate(final Object obj, final Errors errors) {
		Assert.notNull(obj);
		
		/*
		 * user name and email must be given
		 */
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userName", "error.field.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userEmail", "error.field.required");
		
		/*
		 * check, that challenge response is given
		 */
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "recaptcha_response_field", "error.field.required");
	}
}
