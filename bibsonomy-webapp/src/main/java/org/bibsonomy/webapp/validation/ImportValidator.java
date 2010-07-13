package org.bibsonomy.webapp.validation;

import org.bibsonomy.webapp.command.actions.ImportCommand;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

/**
 * @author mwa
 * @version $Id$
 */
public class ImportValidator implements Validator<ImportCommand>{

	@SuppressWarnings("rawtypes")
	@Override
	public boolean supports(final Class clazz) {
		return ImportCommand.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		final ImportCommand command = (ImportCommand) target;
		
		Assert.notNull(command);
		
		Assert.notNull(command.getImportType());
		
		/** look into the for each importType required fields **/
		if("delicious".equals(command.getImportType())) {
			if(command.getUserName().length() == 0){
				errors.rejectValue("userName", "error.field.required");
			}
			if(command.getPassWord().length() == 0){
				errors.rejectValue("passWord", "error.field.required");
			}
		} else if("firefox".equals(command.getImportType())) {
			if(command.getFile() == null || command.getFile().getSize() == 0){
				errors.rejectValue("file", "error.field.required");
			}
		}
	}

}
