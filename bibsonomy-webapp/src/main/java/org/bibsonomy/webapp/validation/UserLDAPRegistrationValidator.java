package org.bibsonomy.webapp.validation;

import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.actions.UserLDAPRegistrationCommand;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 * Validator for UserLDAPRegistrationController
 * 
 * @author Sven Stefani
 * @version $Id$
 */
public class UserLDAPRegistrationValidator implements Validator<UserLDAPRegistrationCommand>{

	@SuppressWarnings("rawtypes")
	@Override
	public boolean supports(Class clazz) {
		return UserLDAPRegistrationCommand.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		UserLDAPRegistrationCommand userObj = (UserLDAPRegistrationCommand) target;
		
		/*
		 * LDAP data has to be entered in the second step
		 */
		if (userObj.getStep() == 2) {			
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "registerUser.name", "error.field.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "registerUser.password", "error.field.required");
		}
		
		/*
		 * username and email are required for successful registration
		 */
		if (userObj.getStep() == 3) {
			/*
			 * Check the user data. 
			 */
			final User user = userObj.getRegisterUser();
			Assert.notNull(user);

			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "registerUser.name", "error.field.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "registerUser.email", "error.field.required");

			/*
			 * validate user
			 */
			errors.pushNestedPath("registerUser");
			ValidationUtils.invokeValidator(new UserValidator(), user, errors);
			errors.popNestedPath();
		
		}
	}	
}