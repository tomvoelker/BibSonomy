package org.bibsonomy.webapp.validation;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.common.enums.Role;
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
	@Override
	public boolean supports(final Class clazz) {
		return UserRegistrationCommand.class.equals(clazz);
	}

	/**
	 * Validates the given userObj.
	 * 
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
	@Override
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
		if (present(command.getCaptchaHTML())) {
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
		if (present(user.getAlgorithm()) || present(user.getApiKey()) ||
				present(user.getIPAddress()) || present(user.getMode()) ||
				present(user.getPrediction()) || present(user.getRegistrationDate()) ||
				present(user.getSpammer()) || present(user.getToClassify()) ||
				present(user.getUpdatedBy()) || present(user.getUpdatedAt()) ||
				!Role.NOBODY.equals(user.getRole())) {
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
		if (!errors.hasFieldErrors("registerUser.password") && 
			!errors.hasFieldErrors("passwordCheck") && 
			!command.getPasswordCheck().equals(user.getPassword())) {
			/*
			 * passwords are not empty and don't match
			 */
			errors.reject("error.field.valid.passwordCheck", "passwords don't match");
		}

		/*
		 * check, that challenge response is given
		 */
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "recaptcha_response_field", "error.field.required");
	}

}
