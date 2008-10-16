package org.bibsonomy.webapp.validation;

import org.bibsonomy.webapp.command.actions.EditBookmarkCommand;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
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
	public void validate(Object postBookmarkObj, Errors errors) {
		/*
		 * To ensure that the received command is not null, we throw an
		 * exception, if this assertion fails.
		 */
		Assert.notNull(postBookmarkObj);
		
		final EditBookmarkCommand bookmark = (EditBookmarkCommand) postBookmarkObj;

		/*
		 * Let's check, that the given user is not null.
		 */
		Assert.notNull(bookmark);

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "postBookmark.resource.url", "error.field.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "postBookmark.resource.title", "error.field.valid.title");
//		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "postBookmark.description", "error.field.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "tags", "error.field.valid.tags");
		
		
		//clean url
		System.out.println("PostBookmarkValidator validate(): " + bookmark.getPostBookmark().getResource().getUrl());
		bookmark.getPostBookmark().getResource().setUrl(UrlUtils.cleanUrl(bookmark.getPostBookmark().getResource().getUrl()));
		
		if (org.bibsonomy.util.ValidationUtils.present(bookmark.getPostBookmark().getResource().getUrl()) 
				&& bookmark.getPostBookmark().getResource().getUrl().startsWith(UrlUtils.BROKEN_URL)) {
			System.out.println("PostBookmarkValidator validate(): BROKEN_URL");
			errors.rejectValue("postBookmark.resource.url", "error.field.valid.url");
		}
		
		/*
		 * TODO: one of the things to add is a check that the group combinations are correct.
		 * Of course, the web interface (forms + JavaScript) enforce correct groups but one 
		 * can easily bypass that. Note that this check additionally has to be added into the 
		 * DBLogics addPost/updatePost, etc. methods!
		 */
		
		System.out.println("--> errors: " + errors.getErrorCount());
		System.out.println("--> errors: " + errors);
	}

}
