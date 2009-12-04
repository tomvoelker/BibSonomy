package org.bibsonomy.webapp.validation;

import org.bibsonomy.webapp.command.SettingsViewCommand;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 * @author daill
 * @version $Id$
 */
public class DeleteUserValidator implements Validator<SettingsViewCommand>{

	@SuppressWarnings("unchecked")
	public boolean supports(Class arg0) {
		return SettingsViewCommand.class.equals(arg0);
	}

	public void validate(Object arg0, Errors errors) {
		// if command is null fail
		Assert.notNull(arg0);
		
		// get the command
		SettingsViewCommand command = (SettingsViewCommand)arg0;
		
		
		// if the delete security string is empty throw an error
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "delete", "error.field.required");
	}

}
