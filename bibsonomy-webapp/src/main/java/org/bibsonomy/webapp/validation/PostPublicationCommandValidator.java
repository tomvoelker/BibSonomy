package org.bibsonomy.webapp.validation;

import org.bibsonomy.webapp.command.actions.PostPublicationCommand;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 * @author ema
 * @version $Id$
 */
public class PostPublicationCommandValidator implements Validator<PostPublicationCommand> {

	@SuppressWarnings("unchecked")
	public boolean supports(Class clazz) {
		return PostPublicationCommand.class.equals(clazz);
	}

	/** This method names the errors of its target items "<resource.getInterHash()>".resource.<XYZ>
	 * 
	 */
	public void validate(final Object target, final Errors errors) {
		final PostPublicationCommand command = (PostPublicationCommand) target;
		errors.pushNestedPath("bibtex");
		/*
		 * validate all publications in the list
		 */
		ValidationUtils.invokeValidator(new ListCommandValidator(), command.getBibtex(), errors);
		errors.popNestedPath();
	}
}

