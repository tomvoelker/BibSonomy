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

	@SuppressWarnings("rawtypes")
	@Override
	public boolean supports(final Class arg0) {
		return PasswordReminderCommand.class.equals(arg0);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		
		Assert.notNull(obj);
		PasswordReminderCommand command = (PasswordReminderCommand) obj;
		
		if (org.bibsonomy.util.ValidationUtils.present(command.getCaptchaHTML())) {
			errors.reject("error.invalid_parameter");
		}
		
		/*
		 * password and user name must be given
		 */
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userName", "error.field.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userEmail", "error.field.required");

		
		/*
		 * check, that challenge response is given
		 */
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "recaptcha_response_field", "error.field.required");
	}
}
