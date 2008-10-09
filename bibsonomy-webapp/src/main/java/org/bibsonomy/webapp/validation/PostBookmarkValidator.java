package org.bibsonomy.webapp.validation;

import org.bibsonomy.webapp.command.actions.EditBookmarkCommand;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 * @author fba
 * @version $Id$
 */
public class PostBookmarkValidator implements Validator<EditBookmarkCommand> {

	public boolean supports(final Class clazz) {
		return EditBookmarkCommand.class.equals(clazz);
	}

	public void validate(Object postBookmarkObj, Errors errors) {
		
		/*
		 * To ensure that the received command is not null, we throw an
		 * exception, if this assertion fails.
		 */
		Assert.notNull(postBookmarkObj);
		
		final EditBookmarkCommand command = (EditBookmarkCommand) postBookmarkObj;
		
		errors.pushNestedPath("postBookmark");


		final EditBookmarkCommand bookmark = (EditBookmarkCommand) postBookmarkObj;

		/*
		 * Let's check, that the given user is not null.
		 */
		Assert.notNull(bookmark);

		/*
		 * Before we make a detailed check on correctness, we look, if required
		 * attributes are set.
		 */
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "resource.url", "error.field.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "resource.title", "error.field.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", "error.field.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "tags", "error.field.required");
				
		
//		if (!resource.hasValidTags()) { 
//			addError ("tags", "please enter valid tags");
//		}
//		if (!resource.isValidurl()) {
//			addError ("url", "please enter a valid URL");
//		}
//		if (!resource.isValidtitle()) {
//			addError("description", "please enter a valid title");
//		}
		
		
		System.out.println("--> errors: " + errors.getErrorCount());
		System.out.println("--> errors: " + errors);
		
		
		
		
		errors.popNestedPath();
	}

}
