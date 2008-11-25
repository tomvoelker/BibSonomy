package org.bibsonomy.webapp.validation;

import java.util.Set;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.util.UrlUtils;
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
		final Post<Bookmark> post = command.getPost();
		
		final Bookmark resource = post.getResource();
		resource.setUrl(UrlUtils.cleanUrl(resource.getUrl()));
		
		final String url = resource.getUrl();
		if (url == null || url.equals("http://") || url.startsWith(UrlUtils.BROKEN_URL)) {
			errors.rejectValue("post.resource.url", "error.field.valid.url");
		}
		
		/*
		 * check groups
		 */
		final String abstractGrouping = command.getAbstractGrouping();
		if ("public".equals(abstractGrouping) || "private".equals(abstractGrouping)) {
			final Set<Group> groups = post.getGroups();
			if (groups != null && !groups.isEmpty()) {
				/*
				 * "public" or "private" selected, but other group chosen
				 */
				errors.rejectValue("post.groups", "error.field.valid.groups");
			}
		} else if ("other".equals(abstractGrouping)) {
			final Set<Group> groups = post.getGroups();
			if (groups == null || groups.isEmpty()) {
				/*
				 * "other" selected, but no group chosen
				 * FIXME: more detailed error messages for different errors
				 */
				errors.rejectValue("post.groups", "error.field.valid.groups");
			}
		} else {
			/*
			 * neither public, private, other chosen
			 */
			errors.rejectValue("post.groups", "error.field.valid.groups");
		}
		
		/*
		 * TODO: one of the things to add is a check that the group combinations are correct.
		 * Of course, the web interface (forms + JavaScript) enforce correct groups but one 
		 * can easily bypass that. Note that this check additionally has to be added into the 
		 * DBLogics addPost/updatePost, etc. methods!
		 */
	
	}

}
