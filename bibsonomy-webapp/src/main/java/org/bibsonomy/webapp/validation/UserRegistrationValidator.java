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

		/*
		 * reCaptchaHTML parameter must not been set!
		 * 
		 * This attribute is only used by the controller to provide the HTML for the
		 * reCaptcha JavaScript. Incoming requests should never have this attribute
		 * set.
		 */
		if (org.bibsonomy.util.ValidationUtils.present(command.getReCaptchaHTML())) {
			errors.reject("error.invalid_parameter");
		}

		/*
		 * Check the user data. 
		 */
		final User user = command.getRegisterUser();
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
				org.bibsonomy.util.ValidationUtils.present(user.getRole()) ||
				org.bibsonomy.util.ValidationUtils.present(user.getSpammer()) ||
				org.bibsonomy.util.ValidationUtils.present(user.getToClassify()) ||
				org.bibsonomy.util.ValidationUtils.present(user.getUpdatedBy()) ||
				org.bibsonomy.util.ValidationUtils.present(user.getUpdatedAt())) {
			errors.reject("error.invalid_parameter");
		}

		/*
		 * validate user
		 */
		errors.pushNestedPath("registerUser");
		ValidationUtils.invokeValidator(new UserValidator(), user, errors);
		errors.popNestedPath();
		
		/*
		 * Check the validity of the supplied passwords.
		 * Both passwords must be non-empty and must match each other. 
		 */
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "registerUser.password", "error.field.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "passwordCheck", "error.field.required");
		if (command.getPasswordCheck() == null || !command.getPasswordCheck().equals(user.getPassword())) {
			/*
			 * passwords don't match
			 */
			errors.reject("error.field.valid.passwordCheck");
		}

		
	}

}
