package org.bibsonomy.webapp.validation;

import org.bibsonomy.webapp.command.SettingsViewCommand;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 * @author cvo
 * @version $Id$
 */
public class ChangePasswordValidator implements Validator<SettingsViewCommand> {

	@SuppressWarnings("unchecked")
	@Override
	public boolean supports(final Class clazz) {
		return SettingsViewCommand.class.equals(clazz);
	}

	@Override
	public void validate(final Object target, final Errors errors) {
		final SettingsViewCommand command = (SettingsViewCommand) target;

		Assert.notNull(command);

		// if one of the password fields is empty or contains only whitespace
		// fail
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "oldPassword", "error.field.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "newPassword", "error.field.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "newPasswordRetype", "error.field.required");

		// if there is no field error on newPassword or passwordCheck but they
		// don't equal fail
		if (!errors.hasFieldErrors("newPassword") && !errors.hasFieldErrors("newPasswordRetype")) {
			if (!command.getNewPassword().equals(command.getNewPasswordRetype())) {
				errors.rejectValue("newPasswordRetype", "error.settings.password.match");
			}
		}
	}
}
