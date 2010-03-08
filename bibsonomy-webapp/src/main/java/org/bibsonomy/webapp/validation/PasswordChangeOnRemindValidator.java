package org.bibsonomy.webapp.validation;

import org.bibsonomy.webapp.command.actions.PasswordChangeOnRemindCommand;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 * @author daill
 * @version $Id$
 */
public class PasswordChangeOnRemindValidator implements Validator<PasswordChangeOnRemindCommand>{

	@SuppressWarnings("unchecked")
	public boolean supports(Class arg0) {
		return PasswordChangeOnRemindCommand.class.equals(arg0);
	}

	public void validate(Object arg0, Errors errors) {
		// if command is null fail
		Assert.notNull(arg0);
		
		// get the command
		final PasswordChangeOnRemindCommand command = (PasswordChangeOnRemindCommand)arg0;
		
		// if one of the password fields is empty or contains only whitespaces fail
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "newPassword", "error.field.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "passwordCheck", "error.field.required");

		// if there is no field error on newPassword or passwordCheck but they don't equal fail
		if(!errors.hasFieldErrors("newPassword") && !errors.hasFieldErrors("passwordCheck") && !command.getNewPassword().equals(command.getPasswordCheck())){
			errors.reject("error.field.valid.passwordCheck", "passwords don't match");
		}		
	}

}
