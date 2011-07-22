package org.bibsonomy.webapp.validation;

import org.bibsonomy.webapp.command.actions.SyncSettingsCommand;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
/**
 * @author rja
 * @version $Id$
 */
public class SyncSettingsValidator implements Validator<SyncSettingsCommand> {

	@SuppressWarnings("unchecked")
	@Override
	public boolean supports(final Class clazz) {
		return SyncSettingsCommand.class.equals(clazz);
	}

	@Override
	public void validate(final Object obj, final Errors errors) {

		Assert.notNull(obj);

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "syncService.service", "error.field.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "syncService.serverUser['userName']", "error.field.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "syncService.serverUser['apiKey']", "error.field.required");
	}

}
