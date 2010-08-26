package org.bibsonomy.webapp.validation.events;

import org.bibsonomy.webapp.command.events.EventRegistrationCommand;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 * @author rja
 * @version $Id$
 */
public class EventRegistrationValidator implements Validator<EventRegistrationCommand> {

	@SuppressWarnings("unchecked")
	@Override
	public boolean supports(Class clazz) {
		return EventRegistrationCommand.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Assert.notNull(target);
		// TODO bib registration command
		if (!(target instanceof EventRegistrationCommand)) return;

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "event.id", "error.field.required");

		
		final EventRegistrationCommand command = (EventRegistrationCommand) target;

		/*
		 * Bibsonomy info - required fields
		 */
		ValidationUtils.rejectIfEmpty(errors, "user.institution", "error.field.required");
		ValidationUtils.rejectIfEmpty(errors, "user.email", "error.field.required");
		//ValidationUtils.rejectIfEmpty(errors, "user.realname", "error.field.required");
				
		/*
		 * Registration required fields
		 */
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "badgename", "error.field.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "address", "error.field.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "subEvent", "error.field.required");		
		
		if (!command.getRegistered()) errors.rejectValue("registered", "error.field.required"); // FIXME: own message!
	}

}
