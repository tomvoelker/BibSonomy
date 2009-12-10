package org.bibsonomy.webapp.validation;

import java.util.List;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 * @author ema
 * @version $Id$
 */
public class PublicationListValidator implements Validator<Post<BibTex>> {

	@Override
	public boolean supports(Class clazz) {
		return Post.class.equals(clazz);
	}

	@Override
	/** This method names the errors of its target items "<resource.getInterHash()>".resource.<XYZ>
	 * 
	 */
	public void validate(Object target, Errors errors) 
	{
		List<Post<BibTex>> posts = (List<Post<BibTex>>) target;
		for(int i=0; i<posts.size(); i++)
		{
			errors.pushNestedPath("list["+i+"]");
			//validate resource
			ValidationUtils.invokeValidator(new PublicationValidator(), posts.get(i).getResource(), errors);
			errors.popNestedPath();
		}
	}
}


