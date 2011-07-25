package org.bibsonomy.webapp.validation;

import org.bibsonomy.webapp.command.SettingsViewCommand;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
/**
 * @author rja
 * @version $Id$
 */
public class SyncSettingsValidator implements Validator<SettingsViewCommand> {

	@SuppressWarnings("unchecked")
	@Override
	public boolean supports(final Class clazz) {
		return SettingsViewCommand.class.equals(clazz);
	}

	/**
	 * FIXME: field errors are not shown on /settings since we don't use Spring's
	 * form binding since the view is filled by another controller. :-(
	 * 
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
	@Override
	public void validate(final Object obj, final Errors errors) {

		Assert.notNull(obj);
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "syncService.service", "error.field.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "syncService.serverUser['userName']", "error.field.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "syncService.serverUser['apiKey']", "error.field.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "syncService.direction", "error.field.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "syncService.resourceType", "error.field.required");
	}

}
