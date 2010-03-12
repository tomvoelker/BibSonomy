package org.bibsonomy.webapp.validation;

import java.util.List;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.actions.PostPublicationCommand;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.validation.Errors;

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
		
		
		final ListCommand<Post<BibTex>> listCommand = command.getBibtex();
		
		//validate resource
		final List<Post<BibTex>> list = listCommand.getList();
		final PostValidator<BibTex> validator = new PostValidator<BibTex>();

		for (int i = 0; i < list.size(); i++) {
			errors.pushNestedPath("list[" + i + "]");
			//validator.validatePost(errors, list.get(i), command.getAbstractGrouping(), command.getGroups());
			validator.validateResource(errors, list.get(i).getResource());
			validator.validateGroups(errors, command.getAbstractGrouping(), command.getGroups());
			errors.popNestedPath();
		}

		errors.popNestedPath();
	}
}

