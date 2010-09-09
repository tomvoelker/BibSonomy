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
		if (!(target instanceof EventRegistrationCommand)) return;

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "event.id", "error.field.required");
		
		final EventRegistrationCommand command = (EventRegistrationCommand) target;
			
		/*
		 * Registration required fields
		 */
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "participantDetails.badgeName", "error.field.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "participantDetails.badgeInstitutionName", "error.field.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "participantDetails.address", "error.field.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "participantDetails.subEvent", "error.field.required");		
		
	}

}
