package org.bibsonomy.webapp.validation;

import org.bibsonomy.model.Group;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;


/**
 *
 * @author niebler
 */
public class GroupValidator implements Validator<Group> {
	
	@Override
	public boolean supports(Class<?> clazz) {
		if (clazz != null) {
			return Group.class.isAssignableFrom(clazz);
		}
		return false;
	}

	@Override
	public void validate(Object target, Errors errors) {
		final Group group = (Group) target;
		Assert.notNull(group);
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", ERROR_FIELD_REQUIRED_KEY);
		if (!errors.hasFieldErrors("name")) {
			UserValidator.validateName(group.getName(), errors, "error.field.valid.user.name"); 
		}
	}
}
