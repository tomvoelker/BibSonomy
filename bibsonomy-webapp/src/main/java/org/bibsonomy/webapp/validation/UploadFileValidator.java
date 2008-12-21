package org.bibsonomy.webapp.validation;

import org.bibsonomy.webapp.command.actions.PasswordReminderCommand;
import org.bibsonomy.webapp.command.actions.UploadFileCommand;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

/**
 * @author daill
 * @version $Id$
 */
public class UploadFileValidator implements Validator<UploadFileCommand> {

	public boolean supports(Class arg0) {
		return UploadFileCommand.class.equals(arg0);
	}

	public void validate(Object obj, Errors arg1) {
		Assert.notNull(obj);
	}

}
