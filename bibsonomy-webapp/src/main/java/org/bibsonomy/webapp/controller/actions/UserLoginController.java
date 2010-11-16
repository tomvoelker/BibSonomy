package org.bibsonomy.webapp.controller.actions;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.webapp.command.actions.UserLoginCommand;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.validation.UserLoginValidator;
import org.bibsonomy.webapp.view.Views;
import org.springframework.security.web.WebAttributes;

/**
 * TODO
 * 
 * @author dzo
 * @version $Id$
 */
public class UserLoginController implements ValidationAwareController<UserLoginCommand> {
	
	private RequestLogic requestLogic;

	@Override
	public UserLoginCommand instantiateCommand() {
		return new UserLoginCommand();
	}

	@Override
	public View workOn(UserLoginCommand command) {
		final Exception lastException = (Exception) this.requestLogic.getSessionAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		
		/*
		 * get last exception from spring security
		 */
		if (present(lastException)) {
			final String messageKey = lastException.getClass().getSimpleName().toLowerCase();
			command.setMessage(messageKey);
		}
		
		return Views.LOGIN;
	}
	
	/**
	 * @param requestLogic the requestLogic to set
	 */
	public void setRequestLogic(RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

	@Override
	public boolean isValidationRequired(UserLoginCommand command) {
		// TODO check
		return true;
	}

	@Override
	public Validator<UserLoginCommand> getValidator() {
		return new UserLoginValidator();
	}
}
