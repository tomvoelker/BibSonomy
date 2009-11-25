package org.bibsonomy.webapp.validation;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.util.UrlUtils;
import org.bibsonomy.webapp.command.actions.EditBookmarkCommand;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
/**
 * @author fba
 * @version $Id$
 */
public class EditBookmarkValidator extends EditPostValidator<Bookmark> {
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean supports(final Class clazz) {
		return EditBookmarkCommand.class.equals(clazz);
	}

	@Override
	protected void validateResource(final Errors errors, final Post<Bookmark> post) {
		final Bookmark resource = post.getResource();
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "post.resource.url", "error.field.required");

		
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
