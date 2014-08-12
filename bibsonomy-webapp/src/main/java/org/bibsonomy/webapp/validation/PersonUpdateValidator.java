package org.bibsonomy.webapp.validation;

import org.bibsonomy.webapp.command.PersonPageCommand;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

/**
 * 
 * @author Christian Pfeiffer
 */
public class PersonUpdateValidator implements Validator<PersonPageCommand> {

	@Override
	public void validate(final Object target, final Errors errors) {
		Assert.notNull(target);
		final PersonPageCommand command = (PersonPageCommand) target;

	}

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		// TODO Auto-generated method stub
		return false;
	}
}
