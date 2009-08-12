package org.bibsonomy.webapp.controller.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.actions.DeleteUserCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
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

	private static final Log log = LogFactory.getLog(DeleteUserController.class);
	
	private LogicInterface logic;
	private Errors errors = null;

	public DeleteUserCommand instantiateCommand() {
		return new DeleteUserCommand();
	}

	public View workOn(DeleteUserCommand command) {
		final RequestWrapperContext context = command.getContext();
		
		/*
		 * user has to be logged in to delete homself
		 */
		if (!context.isUserLoggedIn()){
			errors.reject("error.general.login");
		}
		
		/*
		 * check the ckey
		 */
		if (context.isValidCkey() && !errors.hasErrors()){
			log.debug("User is logged in, ckey is valid ... check the security answer");
			
			/*
			 * check the security input
			 */
			if ("yes".equalsIgnoreCase((command.getDelete()))){
				/*
				 * all fine  ->  delete the user
				 */
				final String loginUserName = context.getLoginUser().getName();
				log.debug("answer is correct - deleting user: " + loginUserName);
				try {
					logic.deleteUser(loginUserName);
				}
				catch (UnsupportedOperationException ex) {
					// this happens when a user is a group
					errors.reject("error.user_is_group_cannot_be_deleted");
				}
			} else {
				/*
				 * ... else throw an error
				 */
				errors.reject("error.secure.answer");
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
