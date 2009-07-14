package org.bibsonomy.webapp.validation;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.util.UrlUtils;
import org.bibsonomy.webapp.command.actions.EditPublicationCommand;
import org.springframework.validation.Errors;
/**
 * @author fba
 * @version $Id$
 */
public class PostPublicationValidator extends PostPostValidator<BibTex> {
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean supports(final Class clazz) {
		return EditPublicationCommand.class.equals(clazz);
	}

	@Override
	protected void validateResource(final Errors errors, final BibTex resource) {
		/*
		 * clean url
		 */
		resource.setUrl(UrlUtils.cleanUrl(resource.getUrl()));
		
		/*
		 * check url
		 */
		final String url = resource.getUrl();
		if (url == null || url.equals("http://") || url.startsWith(UrlUtils.BROKEN_URL)) {
			errors.rejectValue("post.resource.url", "error.field.valid.url");
		}
	}

}
