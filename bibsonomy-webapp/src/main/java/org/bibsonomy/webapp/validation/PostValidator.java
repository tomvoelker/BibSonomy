package org.bibsonomy.webapp.validation;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.webapp.command.actions.EditPostCommand;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
/**
 * @author fba
 * @version $Id$
 * @param <RESOURCE> 
 */
public class PostValidator<RESOURCE extends Resource> implements Validator<EditPostCommand<RESOURCE>> {
	private static final Log log = LogFactory.getLog(PostValidator.class);
	
	@SuppressWarnings("unchecked")
	public boolean supports(final Class clazz) {
		return EditPostCommand.class.isAssignableFrom(clazz);
	}

	/**
	 * Validates the given userObj.
	 * 
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
	public void validate(final Object obj, final Errors errors) {
		/*
		 * To ensure that the received command is not null, we throw an
		 * exception, if this assertion fails.
		 */
		Assert.notNull(obj);
		
		final EditPostCommand<RESOURCE> command = (EditPostCommand<RESOURCE>) obj;

		/*
		 * Let's check, that the given command is not null.
		 */
		Assert.notNull(command);

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "post.resource.title", "error.field.valid.title");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "tags", "error.field.valid.tags");
		
		final Post<RESOURCE> post = command.getPost();
		this.validateResource(errors, post.getResource());

		/*
		 * if no valid (after parsing) tags given, issue an error
		 */
		final Set<Tag> tags = post.getTags();
		if (tags != null && tags.isEmpty() && !errors.hasFieldErrors("tags")) {
			errors.rejectValue("tags", "error.field.valid.tags");
		}
		
		/*
		 * if tag string contains commas, user needs to confirm
		 */
		final String tagString = command.getTags();
		if (tagString != null && (tagString.contains(",") || tagString.contains(";")) && !command.isAcceptComma()) {
			command.setContainsComma(true);
			errors.rejectValue("tags", "error.field.valid.tags.comma");
		}
		
		validateGroups(errors, command.getAbstractGrouping(), command.getGroups());
		
		log.debug("errors in " + EditPostCommand.class.getName() + ": " + errors);
		
	}
	
	private void validateResource(final Errors errors, final RESOURCE resource) {

		/*
		 * validate the resource
		 */
		errors.pushNestedPath("post");
		errors.pushNestedPath("resource");
		if (resource instanceof Bookmark) {
			ValidationUtils.invokeValidator(new BookmarkValidator(), resource, errors);
		} else if (resource instanceof BibTex) {
			ValidationUtils.invokeValidator(new PublicationValidator(), resource, errors);
		}
		errors.popNestedPath(); // resource
		errors.popNestedPath(); // post
	}

	/** Validates the groups from the command. Only some combinations are allowed, e.g., 
	 * either private, public, or other - and with certain other groups only.
	 * 
	 * @param errors
	 * @param abstractGrouping
	 * @param groups
	 */
	private void validateGroups(final Errors errors, final String abstractGrouping, final List<String> groups) {
		log.debug("got abstractGrouping " + abstractGrouping);
		log.debug("got groups " + groups);
		if ("public".equals(abstractGrouping) || "private".equals(abstractGrouping)) {
			if (groups != null && !groups.isEmpty()) {
				/*
				 * "public" or "private" selected, but other group chosen
				 */
				errors.rejectValue("post.groups", "error.field.valid.groups");
			}
		} else if ("other".equals(abstractGrouping)) {
			log.debug("grouping 'other' found ... checking given groups");
			if (groups == null || groups.isEmpty()) {
				log.debug("error: no groups given");
				/*
				 * "other" selected, but no group chosen
				 * TODO: more detailed error messages for different errors
				 */
				errors.rejectValue("post.groups", "error.field.valid.groups");
			}
			/*
			 * TODO: allow multiple groups
			 */
			if (groups.size() > 1) {
				errors.rejectValue("post.groups", "error.field.valid.groups");
			}
		} else {
			log.debug("neither public, private, other chosen");
			/*
			 * neither public, private, other chosen
			 */
			errors.rejectValue("post.groups", "error.field.valid.groups");
		}
	}

}
