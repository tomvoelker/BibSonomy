package org.bibsonomy.webapp.validation;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Bookmark;
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
	private static final Log log = LogFactory.getLog(PostBookmarkValidator.class);
	
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
		
		validateGroups(errors, command.getAbstractGrouping(), command.getGroups());
		
	}

	/** Validates the groups from the command. Only some combinations are allowed, e.g., 
	 * either private, public, or other - and with certain other groups only.
	 * 
	 * @param errors
	 * @param abstractGrouping
	 * @param groups
	 */
	private void validateGroups(Errors errors, final String abstractGrouping, final List<String> groups) {
		log.info("got abstractGrouping " + abstractGrouping);
		log.info("got groups " + groups);
		if ("public".equals(abstractGrouping) || "private".equals(abstractGrouping)) {
			if (groups != null && !groups.isEmpty()) {
				/*
				 * "public" or "private" selected, but other group chosen
				 */
				errors.rejectValue("post.groups", "error.field.valid.groups");
			}
		} else if ("other".equals(abstractGrouping)) {
			if (groups == null || groups.isEmpty()) {
				/*
				 * "other" selected, but no group chosen
				 * TODO: more detailed error messages for different errors
				 */
				errors.rejectValue("post.groups", "error.field.valid.groups");
			}
		} else {
			/*
			 * neither public, private, other chosen
			 */
			errors.rejectValue("post.groups", "error.field.valid.groups");
		}
	}

}
