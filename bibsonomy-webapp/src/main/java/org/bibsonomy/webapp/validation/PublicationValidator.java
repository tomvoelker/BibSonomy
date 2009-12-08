package org.bibsonomy.webapp.validation;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;


/**
 * @author ema
 * @version $Id$
 */
public class PublicationValidator implements Validator<Post<BibTex>> {

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
		Post<BibTex> post = (Post<BibTex>) target;
		
		errors.pushNestedPath("Post<BibTex>"+post.getResource().getInterHash());
		//validate resource
		ValidationUtils.invokeValidator(new BibTexValidator(), post.getResource(), errors);
		/* HANDLED BY DATABASE EXCEPTION+ERRORMESSAGE
		//validate tags
		ValidationUtils.invokeValidator(new TagValidator(), post.getTags(), errors);*/
		errors.popNestedPath();
	}
}

