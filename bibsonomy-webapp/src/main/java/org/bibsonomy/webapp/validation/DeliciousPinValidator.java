package org.bibsonomy.webapp.validation;

import org.bibsonomy.webapp.command.actions.DeliciousPinCommand;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

/**
 * @author mwa
 * @version $Id$
 */
public class DeliciousPinValidator implements Validator<DeliciousPinCommand>{

	@SuppressWarnings("rawtypes")
	@Override
	public boolean supports(final Class clazz) {
		return DeliciousPinCommand.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		final DeliciousPinCommand command = (DeliciousPinCommand) target;
		
		Assert.notNull(command);
		
		if(
				command.getImportData() == null ||
				("posts".equals(command.getImportData()) == false && "bundles".equals(command.getImportData()) == false)) {
			errors.rejectValue("importData", "error.field.required");
		}
	}

}
