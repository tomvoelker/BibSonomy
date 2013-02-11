package org.bibsonomy.webapp.validation;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;
import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.actions.SamlUserIDRegistrationCommand;
import org.bibsonomy.webapp.command.actions.UserIDRegistrationCommand;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.util.spring.security.exceptions.SpecialAuthMethodRequiredException;
import org.bibsonomy.webapp.util.spring.security.handler.FailureHandler;
import org.bibsonomy.webapp.util.spring.security.saml.SamlAuthenticationTool;
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

	private static final String COMMAND_STORE_KEY = UserSamlRegistrationValidator.class.getName() + ".COMMAND_STORE";
	private final SamlAuthenticationTool samlAuthTool;
	private final RequestLogic requestLogic;
	
	
	/**
	 * @param samlAuthTool authtool to use
	 * @param requestLogic 
	 */
	public UserSamlRegistrationValidator(SamlAuthenticationTool samlAuthTool, RequestLogic requestLogic) {
		this.samlAuthTool = samlAuthTool;
		org.bibsonomy.util.ValidationUtils.assertNotNull(samlAuthTool);
		this.requestLogic = requestLogic;
		org.bibsonomy.util.ValidationUtils.assertNotNull(requestLogic);
	}
	
	@Override
	public boolean supports(Class<?> clazz) {
		return SamlUserIDRegistrationCommand.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		SamlUserIDRegistrationCommand userObj = (SamlUserIDRegistrationCommand) target;
		
		if (userObj.getStep() > 2) {
			// allow only the remoteuser herself to register -> ensure fresh saml login

			Object utbr = requestLogic.getSessionAttribute(FailureHandler.USER_TO_BE_REGISTERED);
			try {
				samlAuthTool.ensureFreshAuthentication();
			} catch (SpecialAuthMethodRequiredException e) {
				requestLogic.setSessionAttribute(FailureHandler.USER_TO_BE_REGISTERED, utbr);
				requestLogic.setSessionAttribute(COMMAND_STORE_KEY, target);
				throw e;
			}
			Object oldCommand = requestLogic.getSessionAttribute(COMMAND_STORE_KEY);
			try {
				BeanUtils.copyProperties(target, oldCommand);
				requestLogic.removeSessionAttribute(COMMAND_STORE_KEY);
			} catch (IllegalAccessException ex) {
				throw new RuntimeException("cannot copy old command properties", ex);
			} catch (InvocationTargetException ex) {
				throw new RuntimeException("cannot copy old command properties", ex);
			}
		}
		
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