package org.bibsonomy.webapp.validation;

import org.bibsonomy.webapp.command.actions.UserLDAPRegistrationCommand;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 * Validator for UserLDAPRegistrationController
 * 
 * @author Sven Stefani
 * @version $Id$
 */
public class UserLDAPRegistrationValidator implements Validator<UserLDAPRegistrationCommand>{

	public boolean supports(Class clazz) {
		return UserLDAPRegistrationCommand.class.equals(clazz);
	}

	public void validate(Object target, Errors errors) {
		UserLDAPRegistrationCommand userObj = (UserLDAPRegistrationCommand) target;
		
		/*
		 * OpeneID has to be entered in the second step
		 */
		if (userObj.getStep() == 2) {			
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "registerUser.name", "error.field.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "registerUser.password", "error.field.required");
		}
		
		/*
		 * username and email are required for succesful registration
		 */
		if (userObj.getStep() == 3) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "registerUser.name", "error.field.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "registerUser.email", "error.field.required");
		}
	}	
}