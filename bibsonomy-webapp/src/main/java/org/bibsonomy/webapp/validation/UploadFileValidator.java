package org.bibsonomy.webapp.validation;

import org.bibsonomy.webapp.command.actions.UploadFileCommand;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

/**
 * @author daill
 * @version $Id$
 */
public class UploadFileValidator implements Validator<UploadFileCommand> {

	@SuppressWarnings("unchecked")
	public boolean supports(final Class arg0) {
		return UploadFileCommand.class.equals(arg0);
	}

	public void validate(Object obj, Errors arg1) {
		Assert.notNull(obj);
		
		UploadFileCommand command = (UploadFileCommand) obj;
		Errors errors = (Errors) arg1;

		if (command.getResourceHash() == null){
			errors.reject("error.uploadFile.hash");
		}
	}

}
