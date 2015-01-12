/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bibsonomy.webapp.validation;

import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Group;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import static org.bibsonomy.util.ValidationUtils.present;

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
			validateName(group.getName(), errors); 
		}
	}
	
	private void validateName (final String name, final Errors errors) {
		if (!present(name) ||
				"public".equals(name) ||
				"private".equals(name) ||
				"friends".equals(name) ||
				"null".equals(name) ||
				name.length() > UserValidator.USERNAME_MAX_LENGTH ||
				UserValidator.USERNAME_DISALLOWED_CHARACTERS_PATTERN.matcher(name).find()) {
			LogFactory.getLog(GroupValidator.class).error(UserValidator.USERNAME_DISALLOWED_CHARACTERS_PATTERN.matcher(name).find());
			errors.rejectValue("name","error.field.valid.user.name");
		}
	}
}
