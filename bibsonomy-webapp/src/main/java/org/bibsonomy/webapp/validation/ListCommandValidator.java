package org.bibsonomy.webapp.validation;

import java.util.List;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 * @author ema
 * @version $Id$
 * @param <RESOURCE> The type of resource contained in the ListCommand.
 */
public class ListCommandValidator<RESOURCE extends Resource> implements Validator<ListCommand<Post<RESOURCE>>> {

	@SuppressWarnings("unchecked")
	public boolean supports(Class clazz) {
		return ListCommand.class.equals(clazz);
	}

	/** This method names the errors of its target items "<resource.getInterHash()>".resource.<XYZ>
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void validate(final Object target, final Errors errors) {
		final ListCommand<Post<RESOURCE>> listCommand = (ListCommand<Post<RESOURCE>>) target;
		
		//validate resource
		final List<Post<RESOURCE>> list = listCommand.getList();

		for (int i = 0; i < list.size(); i++) {
			errors.pushNestedPath("list[" + i + "]");
			ValidationUtils.invokeValidator(new PostValidator<RESOURCE>(), list.get(i), errors);
			errors.popNestedPath();
		}
		
	}
}


