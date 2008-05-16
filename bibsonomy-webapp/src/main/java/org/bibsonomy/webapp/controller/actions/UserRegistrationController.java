package org.bibsonomy.webapp.controller.actions;

import org.apache.log4j.Logger;
import org.bibsonomy.model.User;
import org.bibsonomy.model.UserSettings;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.actions.UserRegistrationCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.validation.UserRegistrationValidator;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/** This controller handles the registration of users.
 * TODO: currently this is just a mockup: nothing really happens.
 * 
 * @author rja
 * @version $Id$
 */
public class UserRegistrationController implements MinimalisticController<UserRegistrationCommand>, ErrorAware, ValidationAwareController<UserRegistrationCommand> {
	private static final Logger log = Logger.getLogger(UserRegistrationController.class);
	
	protected LogicInterface logic;	
	protected UserSettings userSettings;
	private Errors errors = null;

	
	/**
	 * @param logic - an instance of the logic interface.
	 */
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}

	/** 
	 * Returns an instance of the command the controller handles.
	 * 
	 * @see org.bibsonomy.webapp.util.MinimalisticController#instantiateCommand()
	 */
	public UserRegistrationCommand instantiateCommand() {
		final UserRegistrationCommand userRegistrationCommand = new UserRegistrationCommand();
		/*
		 * add user to command
		 */
		userRegistrationCommand.setRegisterUser(new User());
		return userRegistrationCommand;
	}

	
	public View workOn(UserRegistrationCommand command) {
		log.fatal("workOn() called");

		/*
		 * TODO: implement something useful
		 */
//		final User user = registerUser;
//		errors.pushNestedPath("registerUser");
//		new UserValidator().validate(user, errors);
//		errors.popNestedPath();
//		
//		errors.reject("no valid registration details found");
		
		return Views.REGISTER_USER;
	}

	public Errors getErrors() {
		return errors;
	}

	public void setErrors(final Errors errors) {
		this.errors = errors;
	}

	/** Returns, if validation is required for the given command. On default,
	 * for all incoming data validation is required.
	 * 
	 * @see org.bibsonomy.webapp.util.ValidationAwareController#isValidationRequired(java.lang.Object)
	 */
	public boolean isValidationRequired(final UserRegistrationCommand command) {
		/*
		 * is validation always required?
		 */
//		final User user = command.getRegisterUser();
//		new UserValidator().validate(user, errors);
		

		return true;
	}

	public Validator<UserRegistrationCommand> getValidator() {
		return new UserRegistrationValidator();
	}
	
}
