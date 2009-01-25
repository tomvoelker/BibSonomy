package org.bibsonomy.webapp.controller.actions;

import org.apache.log4j.Logger;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.actions.DeleteUserCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.validation.DeleteUserValidator;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * @author daill
 * @version $Id$
 */
public class DeleteUserController implements MinimalisticController<DeleteUserCommand>, ErrorAware, ValidationAwareController<DeleteUserCommand> {

	private static final Logger log = Logger.getLogger(DeleteUserController.class);
	
	private LogicInterface logic;
	private Errors errors = null;

	public DeleteUserCommand instantiateCommand() {
		return new DeleteUserCommand();
	}

	public View workOn(DeleteUserCommand command) {
		
		// user have to be logged in to delete homself
		if (!command.getContext().getUserLoggedIn()){
			errors.reject("error.general.login");
		}

		
		
		// check the ckey
		if (command.getContext().isValidCkey() && !errors.hasErrors()){
			log.debug("User is logged in, check the ckey");
			
			// check the security input
			if ("yes".equalsIgnoreCase((command.getDelete()))){
				// if all fine delete the user
				log.debug("Ckey is correct - deleting user: " + command.getContext().getLoginUser().getName());
				logic.deleteUser(command.getContext().getLoginUser().getName());
			} else {
				// ... else throw an error
				errors.reject("error.field.valid.ckey");
			}
		} else {
			errors.reject("error.field.valid.ckey");
		}
		

		if (errors.hasErrors()){
			return Views.ERROR;
		}
		
		return new ExtendedRedirectView("/logout");
	}
	
	/**
	 * @param logic
	 */
	public void setLogic(final LogicInterface logic) {
		this.logic = logic;
	}

	public Errors getErrors() {
		return this.errors;
	}

	public void setErrors(final Errors errors) {
		this.errors = errors;
	}

	public Validator<DeleteUserCommand> getValidator() {
		return new DeleteUserValidator();
	}

	public boolean isValidationRequired(DeleteUserCommand command) {
		return true;
	}	

}
