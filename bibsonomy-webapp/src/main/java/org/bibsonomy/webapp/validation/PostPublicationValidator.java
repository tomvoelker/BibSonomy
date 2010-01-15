package org.bibsonomy.webapp.validation;

import org.bibsonomy.webapp.command.actions.PostPublicationCommand;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 * @author ema
 * @version $Id$
 */
public class PostPublicationValidator implements Validator<PostPublicationCommand> {

	@Override
	public boolean supports(Class clazz) {
		return PostPublicationCommand.class.equals(clazz);
	}

	@Override
	/** This method names the errors of its target items "<resource.getInterHash()>".resource.<XYZ>
	 * 
	 */
	public void validate(Object target, Errors errors) 
	{
		PostPublicationCommand command = (PostPublicationCommand) target;
		errors.pushNestedPath("bibtex");
		//validate resource
		ValidationUtils.invokeValidator(new PublicationListCommandValidator(), command.getBibtex(), errors);
		errors.popNestedPath();
	}
}

