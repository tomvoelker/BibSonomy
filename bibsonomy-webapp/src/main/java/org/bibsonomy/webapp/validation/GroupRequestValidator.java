package org.bibsonomy.webapp.validation;

import org.bibsonomy.model.Group;
import org.bibsonomy.webapp.command.GroupRequestCommand;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 * validator for group requests
 * 
 * @author Mario Holtmueller
 * @author dzo
 */
public class GroupRequestValidator implements Validator<GroupRequestCommand> {

	@Override
	public boolean supports(Class<?> clazz) {
		return GroupRequestCommand.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Assert.notNull(target);
		final GroupRequestCommand command = (GroupRequestCommand) target;
		
		errors.pushNestedPath("group");
		
		final Group group = command.getGroup();
		
		/*
		 * delegate other check to the group validator
		 */
		ValidationUtils.invokeValidator(new GroupValidator(), group, errors);
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", ERROR_FIELD_REQUIRED_KEY);
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "groupRequest.reason", ERROR_FIELD_REQUIRED_KEY);
		
		errors.popNestedPath();
	}
}
