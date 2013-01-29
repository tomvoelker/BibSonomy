package org.bibsonomy.webapp.validation;

import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.actions.SamlUserIDRegistrationCommand;
import org.bibsonomy.webapp.command.actions.UserIDRegistrationCommand;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 * Validator for UserSamlRegistrationController
 * 
 * @author jensi
 * @version $Id$
 */
public class UserSamlRegistrationValidator implements Validator<UserIDRegistrationCommand>{

	@Override
	public boolean supports(Class<?> clazz) {
		return SamlUserIDRegistrationCommand.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		UserIDRegistrationCommand userObj = (UserIDRegistrationCommand) target;
		
		/*
		 * username and email are required for successful registration
		 */
		if (userObj.getStep() != 2) {
			/*
			 * Check the user data. 
			 */
			final User user = userObj.getRegisterUser();
			Assert.notNull(user);

			/*
			 * validate user
			 */
			errors.pushNestedPath("registerUser");
			ValidationUtils.invokeValidator(new UserValidator(), user, errors);
			errors.popNestedPath();
		}
	}	
}