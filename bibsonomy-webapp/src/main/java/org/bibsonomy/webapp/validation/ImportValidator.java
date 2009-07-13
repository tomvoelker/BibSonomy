package org.bibsonomy.webapp.validation;

import org.bibsonomy.webapp.command.actions.ImportCommand;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.validation.Errors;
import org.springframework.util.Assert;

/**
 * @author mwa
 * @version $Id$
 */
public class ImportValidator implements Validator<ImportCommand>{

	@SuppressWarnings("unchecked")
	public boolean supports(final Class clazz) {
		return ImportCommand.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ImportCommand command = (ImportCommand) target;
		
		Assert.notNull(command);
		
		/** look into the for each importType required fields **/
		if(command.getImportType().equals("delicious")){
			if(command.getUserName().length() == 0){
				errors.rejectValue("userName", "error.field.required");
			}
			if(command.getPassWord().length() == 0){
				errors.rejectValue("passWord", "error.field.required");
			}
		}else if(command.getImportType().equals("firefox")){
			if(command.getFile() == null || command.getFile().getSize() == 0){
				errors.rejectValue("file", "error.field.required");
			}
		}
	}

}
