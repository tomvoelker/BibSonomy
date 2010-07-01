package org.bibsonomy.webapp.validation;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.util.GroupUtils;
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
	
	private static final Group PUBLIC_GROUP = GroupUtils.getPublicGroup();
	private static final Group PRIVATE_GROUP = GroupUtils.getPrivateGroup();
	
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
		
		@SuppressWarnings("unchecked")
		final EditPostCommand<RESOURCE> command = (EditPostCommand<RESOURCE>) obj;
		/*
		 * Let's check, that the given command is not null.
		 */
		Assert.notNull(command);

		this.validatePost(errors, command.getPost(), command.getAbstractGrouping(), command.getGroups());
		
		this.validateTags(errors, command);
	}

	/**
	 * @param errors
	 * @param command
	 */
	protected void validateTags(final Errors errors, final EditPostCommand<RESOURCE> command) {
		/*
		 * validate tags
		 */
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "tags", "error.field.valid.tags");
		/*
		 * if no valid (after parsing) tags given, issue an error
		 */
		final Set<Tag> tags = command.getPost().getTags();
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
		
		log.debug("errors in " + EditPostCommand.class.getName() + ": " + errors);
	}

	/**
	 * Validates the post, in particular the resource it contains and 
	 * the setting of the group selection box.
	 * 
	 * @param errors
	 * @param post
	 * @param abstractGrouping
	 * @param groups
	 */
	protected void validatePost(final Errors errors, final Post<RESOURCE> post, final String abstractGrouping, final List<String> groups) {
		errors.pushNestedPath("post");
		validateResource(errors, post.getResource());
		errors.popNestedPath(); // post

		validateGroups(errors, abstractGrouping, groups);
	}
	
	/**
	 * Validates the given resource.
	 * 
	 * @param errors
	 * @param resource
	 */
	protected void validateResource(final Errors errors, final RESOURCE resource) {
		/*
		 * validate the resource
		 */
		errors.pushNestedPath("resource");
		/*
		 * every resource has a title ...
		 */
		if (!present(resource.getTitle())) {
			errors.rejectValue("title", "error.field.valid.title");
		}
		/*
		 * resource-specific checks
		 */
		if (resource instanceof Bookmark) {
			ValidationUtils.invokeValidator(new BookmarkValidator(), resource, errors);
		} else if (resource instanceof BibTex) {
			ValidationUtils.invokeValidator(new PublicationValidator(), resource, errors);
		}
		errors.popNestedPath(); // resource
	}

	/** Validates the groups from the command. Only some combinations are allowed, e.g., 
	 * either private, public, or other - and with certain other groups only.
	 * 
	 * @param errors
	 * @param abstractGrouping
	 * @param groups
	 */
	protected void validateGroups(final Errors errors, final String abstractGrouping, final List<String> groups) {
		log.debug("got abstractGrouping " + abstractGrouping);
		log.debug("got groups " + groups);
		if (PUBLIC_GROUP.getName().equals(abstractGrouping) || PRIVATE_GROUP.getName().equals(abstractGrouping)) {
			if (present(groups)) {
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
