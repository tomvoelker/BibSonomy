package org.bibsonomy.webapp.validation;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.model.DiscussionItem;
import org.bibsonomy.webapp.command.ajax.DiscussionItemAjaxCommand;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 * @author dzo
 * @version $Id$
 * @param <D> 
 */
public abstract class DiscussionItemValidator<D extends DiscussionItem> implements Validator<DiscussionItemAjaxCommand<D>> {

	protected static final String DISCUSSION_ITEM_PATH = "discussionItem.";
	
	@Override
	public boolean supports(final Class<?> clazz) {
		return DiscussionItemAjaxCommand.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(final Object target, final Errors errors) {
		@SuppressWarnings("unchecked")
		final DiscussionItemAjaxCommand<D> command = (DiscussionItemAjaxCommand<D>) target;
		
		/*
		 * a hash must be provided
		 */
		if (!present(command.getHash())) {
			errors.rejectValue("hash", "error.field.valid.hash");
		}
		
		/*
		 * validate item
		 */
		this.validateDiscussionItem(command.getDiscussionItem(), errors);
		
		/*
		 * validate groups
		 */
		ValidationUtils.invokeValidator(new GroupingValidator(), command, errors);
	}

	protected abstract void validateDiscussionItem(D discussionItem, Errors errors);

}
