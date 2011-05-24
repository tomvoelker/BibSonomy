package org.bibsonomy.webapp.validation;

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
		
		if (!present(command.getImportData()) || ( !"posts".equals(command.getImportData()) && !"bundles".equals(command.getImportData()) )) {
			errors.rejectValue("importData", "error.field.required");
		}
	}

}
