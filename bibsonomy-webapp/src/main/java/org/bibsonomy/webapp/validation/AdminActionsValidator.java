package org.bibsonomy.webapp.validation;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.bibsonomy.common.enums.ClassifierSettings;
import org.bibsonomy.webapp.command.ajax.AdminAjaxCommand;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

/**
 * Validates fields related to admin tasks
 * @author bkr
 * @version $Id$
 */
public class AdminActionsValidator implements Validator<AdminAjaxCommand> {
	
	@SuppressWarnings("rawtypes")
	@Override
	public boolean supports(final Class clazz) {
		return AdminAjaxCommand.class.equals(clazz);
	}

	@Override
	public void validate(final Object settingsObj, final Errors errors) {
		/*
		 * To ensure that the received command is not null, we throw an
		 * exception, if this assertion fails.
		 */
		Assert.notNull(settingsObj);
		final AdminAjaxCommand command = (AdminAjaxCommand) settingsObj;
		
		/*
		 * Validate regular expression
		 */
		if (command.getKey() != null) {
			if (ClassifierSettings.WHITELIST_EXP.equals(ClassifierSettings
					.getClassifierSettings(command.getKey()))) {
				if (!errors.hasFieldErrors("value"))
					validateRegExForWhitelist(command.getValue(), errors);
			}
		}
	}

	/**
	 * Validates the correctness of the regular expression for the whitelist The
	 * regular expression is compiled - if an exception is thrown, the pattern
	 * is not valid
	 * 
	 * @param regular
	 *            expression
	 * @param errors
	 */
	private void validateRegExForWhitelist(final String regex, final Errors errors) {
		if (!present(regex)) {
			errors.rejectValue("value", "error.field.valid.admin.whitelist");
		} else {

			try {
				Pattern.compile(regex);
			} catch (final PatternSyntaxException e) {
				errors.rejectValue("value", "error.field.valid.admin.whitelist");
			}
		}

	}

}
