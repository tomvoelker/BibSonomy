package org.bibsonomy.webapp.validation;

import org.bibsonomy.webapp.command.actions.EditBookmarkCommand;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.util.UrlUtils;
/**
 * @author fba
 * @version $Id$
 */
public class PostBookmarkValidator implements Validator<EditBookmarkCommand> {

	@SuppressWarnings("unchecked")
	public boolean supports(final Class clazz) {
		return EditBookmarkCommand.class.equals(clazz);
	}

	/**
	 * Validates the given userObj.
	 * 
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
	public void validate(Object obj, Errors errors) {
		/*
		 * To ensure that the received command is not null, we throw an
		 * exception, if this assertion fails.
		 */
		Assert.notNull(obj);
		
		final EditBookmarkCommand command = (EditBookmarkCommand) obj;

		/*
		 * Let's check, that the given command is not null.
		 */
		Assert.notNull(command);

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "post.resource.url", "error.field.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "post.resource.title", "error.field.valid.title");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "tags", "error.field.valid.tags");
		
		
		// clean url
		final Bookmark resource = command.getPost().getResource();
		resource.setUrl(UrlUtils.cleanUrl(resource.getUrl()));
		
		final String url = resource.getUrl();
		if (url == null || url.equals("http://") || url.startsWith(UrlUtils.BROKEN_URL)) {
			errors.rejectValue("post.resource.url", "error.field.valid.url");
		}
		
		/*
		 * TODO: one of the things to add is a check that the group combinations are correct.
		 * Of course, the web interface (forms + JavaScript) enforce correct groups but one 
		 * can easily bypass that. Note that this check additionally has to be added into the 
		 * DBLogics addPost/updatePost, etc. methods!
		 */
	
	}

}
