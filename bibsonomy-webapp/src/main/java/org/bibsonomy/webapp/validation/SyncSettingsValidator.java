package org.bibsonomy.webapp.validation;

import static org.bibsonomy.util.ValidationUtils.present;

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

			if (present(command.getNewSyncServer())) {
				/*
				 * user wants to create a new sync server -> validate this one
				 */
				errors.pushNestedPath("newSyncServer");
				validateSyncServer(errors);
				errors.popNestedPath();
			} else {
				/* 
				 * user wants to delete or update an existing server
				 */
				ValidationUtils.rejectIfEmpty(errors, "syncServer", "error.field.required");
				final List<SyncService> syncServer = command.getSyncServer();
				Assert.notEmpty(syncServer);

				for (int i = 0; i < syncServer.size(); i++) {
					/*
					 * FIXME: Spring always creates an object for all list
					 * elements, even if no form fields for that list index
					 * have been submitted. Thus, we don't check 
					 * <code>present(syncServer.get(i))</code> but:
					 */
					if (present(syncServer.get(i).getService())) {
						/*
						 * this service was specified --> check it 
						 */
						errors.pushNestedPath("syncServer[" + i + "]");
						validateSyncServer(errors);
						errors.popNestedPath();
					}
				}
			}
		}
	}

	private void validateSyncServer(final Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "service", "error.field.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "serverUser['userName']", "error.field.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "serverUser['apiKey']", "error.field.required");
//		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "direction", "error.field.required");
//		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "resourceType", "error.field.required");
	}
	
}
