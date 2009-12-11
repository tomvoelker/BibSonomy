package org.bibsonomy.webapp.validation;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 * @author ema
 * @version $Id$
 */
public class PublicationListCommandValidator implements Validator<ListCommand<Post<BibTex>>> {

	@Override
	public boolean supports(Class clazz) {
		return ListCommand.class.equals(clazz);
	}

	@Override
	/** This method names the errors of its target items "<resource.getInterHash()>".resource.<XYZ>
	 * 
	 */
	public void validate(Object target, Errors errors) 
	{
		ListCommand<Post<BibTex>> listCommand = (ListCommand<Post<BibTex>>) target;
		
		//validate resource
		for(int i=0; i<listCommand.getList().size(); i++)
		{
			errors.pushNestedPath("list["+i+"]");
			ValidationUtils.invokeValidator(new PublicationValidator(), listCommand.getList().get(i), errors);
			errors.popNestedPath();
		}
		
	}
}


