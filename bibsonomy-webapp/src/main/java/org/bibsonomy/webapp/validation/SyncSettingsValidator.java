package org.bibsonomy.webapp.validation;

import java.util.List;

import org.bibsonomy.model.sync.SyncService;
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
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
	@Override
	public void validate(final Object obj, final Errors errors) {

		Assert.notNull(obj);
		if (obj instanceof SettingsViewCommand) {
			final SettingsViewCommand command = (SettingsViewCommand) obj;
			final List<SyncService> syncServer = command.getSyncServer();
			Assert.notNull(syncServer);
			Assert.notEmpty(syncServer);
			for (int i = 0; i < syncServer.size(); i++) {
				errors.pushNestedPath("syncServer[" + i + "]");
				ValidationUtils.rejectIfEmptyOrWhitespace(errors, "service", "error.field.required");
				ValidationUtils.rejectIfEmptyOrWhitespace(errors, "serverUser['userName']", "error.field.required");
				ValidationUtils.rejectIfEmptyOrWhitespace(errors, "serverUser['apiKey']", "error.field.required");
//				ValidationUtils.rejectIfEmptyOrWhitespace(errors, "direction", "error.field.required");
//				ValidationUtils.rejectIfEmptyOrWhitespace(errors, "resourceType", "error.field.required");
				errors.popNestedPath();
			}
			
		}
	}

}
