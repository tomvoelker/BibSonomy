package org.bibsonomy.webapp.validation;

import org.bibsonomy.webapp.command.actions.UserOpenIDLdapRegistrationCommand;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 * Validator for UserOpenIDRegistrationController
 * 
 * @author Stefan Stützer
 * @version $Id$
 */
public class UserOpenIDRegistrationValidator implements Validator<UserOpenIDLdapRegistrationCommand>{

	@SuppressWarnings("rawtypes")
	@Override
	public boolean supports(Class clazz) {
		return UserOpenIDLdapRegistrationCommand.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		UserOpenIDLdapRegistrationCommand userObj = (UserOpenIDLdapRegistrationCommand) target;
		
		/*
		 * OpeneID has to be entered in the second step
		 */
		if (userObj.getStep() == 2) {			
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "registerUser.openID", "error.field.required");
		}
		
		/*
		 * username and email are required for succesful registration
		 */
		if (userObj.getStep() == 4) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "registerUser.name", "error.field.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "registerUser.email", "error.field.required");
		}
	}	
}