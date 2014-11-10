package org.bibsonomy.webapp.validation.ajax;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.webapp.command.ajax.ClipboardManagerCommand;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.validation.Errors;

/**
 * validator for ajax clipboard requests
 *
 * @author vhem, dzo
 */
public class ClipboardValidator implements Validator<ClipboardManagerCommand>{

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		return ClipboardManagerCommand.class.equals(clazz);
	}

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Object target, Errors errors) {
		final ClipboardManagerCommand command = (ClipboardManagerCommand) target;
		final String action = command.getAction();
		if (!present(action)) {
			errors.reject("error.action.valid");
		}
		
		final String user = command.getUser();
		if (!present(user)) {
			errors.rejectValue("user", "error.user.valid");
		}
		
		final String hash = command.getHash();
		if (!present(hash)) {
			errors.rejectValue("hash", "error.hash.valid");
		}
	}

}
