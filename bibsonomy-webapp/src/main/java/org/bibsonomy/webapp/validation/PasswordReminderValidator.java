package org.bibsonomy.webapp.validation;



import org.bibsonomy.common.enums.Role;
import org.bibsonomy.model.User;
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

	@SuppressWarnings("unchecked")
	public boolean supports(final Class arg0) {
		return PasswordReminderCommand.class.equals(arg0);
	}

	public void validate(Object obj, Errors errors) {
		
		Assert.notNull(obj);
		PasswordReminderCommand command = (PasswordReminderCommand) obj;
		
		if (org.bibsonomy.util.ValidationUtils.present(command.getCaptchaHTML())) {
			errors.reject("error.invalid_parameter");
		}
		
		/*
		 * Check the user data. 
		 */
		final User user = command.getRequestedUser();
		Assert.notNull(user);

		/*
		 * TODO: Check, that ONLY values are set, which the user can enter in a form,
		 * i.e. that no spammer status or other settings are set!
		 */
		if (org.bibsonomy.util.ValidationUtils.present(user.getAlgorithm()) ||
				org.bibsonomy.util.ValidationUtils.present(user.getApiKey()) ||
				org.bibsonomy.util.ValidationUtils.present(user.getIPAddress()) ||
				org.bibsonomy.util.ValidationUtils.present(user.getMode()) ||
				org.bibsonomy.util.ValidationUtils.present(user.getPrediction()) ||
				org.bibsonomy.util.ValidationUtils.present(user.getRegistrationDate()) ||
				org.bibsonomy.util.ValidationUtils.present(user.getSpammer()) ||
				org.bibsonomy.util.ValidationUtils.present(user.getToClassify()) ||
				org.bibsonomy.util.ValidationUtils.present(user.getUpdatedBy()) ||
				org.bibsonomy.util.ValidationUtils.present(user.getUpdatedAt()) ||
				org.bibsonomy.util.ValidationUtils.present(user.getReminderPassword()) ||
				org.bibsonomy.util.ValidationUtils.present(user.getReminderPasswordRequestDate()) ||
				org.bibsonomy.util.ValidationUtils.present(user.getPassword()) ||
				!user.getRole().equals(Role.NOBODY)	
		) {
			errors.reject("error.invalid_parameter");
		}
		
		/*
		 * validate user
		 */
		errors.pushNestedPath("requestedUser");
		ValidationUtils.invokeValidator(new UserValidator(), user, errors);
		errors.popNestedPath();
		
		/*
		 * check, that challenge response is given
		 */
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "recaptcha_response_field", "error.field.required");
	}
}
