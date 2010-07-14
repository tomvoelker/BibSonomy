package org.bibsonomy.webapp.controller.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.UserUpdateOperation;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.webapp.command.actions.PasswordChangeOnRemindCommand;
import org.bibsonomy.webapp.util.CookieAware;
import org.bibsonomy.webapp.util.CookieLogic;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.RequestAware;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.validation.PasswordChangeOnRemindValidator;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * 
 * TODO: bitte hier reinschreiben, was das Ding macht!
 * 
 * @author daill
 * @version $Id$
 */
public class PasswordChangeOnRemindController implements ErrorAware, ValidationAwareController<PasswordChangeOnRemindCommand>, RequestAware, CookieAware{
	private static final Log log = LogFactory.getLog(PasswordChangeOnRemindController.class);

	private LogicInterface adminLogic;
	private CookieLogic cookieLogic;
	private RequestLogic requestLogic;

	private Errors errors;

	@Override
	public View workOn(PasswordChangeOnRemindCommand command) {
		log.debug("starting work");
		command.setPageTitle("password change");

		
		/*
		 * the name of the user we want to update
		 */
		final String userName = (String)requestLogic.getSessionAttribute("tmpUser");

		/*
		 * check, if user name is available
		 */
		if (!ValidationUtils.present(userName)) {
			/*
			 * this should never happen - except user has manipulated the form
			 */
			errors.reject("error.method_not_allowed");
			return Views.ERROR;
		}

		/*
		 * set user name into command to show it in form field
		 */
		command.setUserName(userName);
		
		/*
		 * if there are any errors show them
		 */
		if (errors.hasErrors()) {
			return Views.PASSWORD_CHANGE_ON_REMIND;
		}


		// create the md5 hash of the new password
		final String hashedPassword = StringUtils.getMD5Hash(command.getNewPassword());

		/*
		 * build the user we want to update
		 */
		final User user = new User();
		user.setName(userName);
		user.setPassword(hashedPassword);

		log.debug("writing the new password to the database");
		// update user in database
		adminLogic.updateUser(user, UserUpdateOperation.UPDATE_PASSWORD);

		log.debug("setting up new cookie");
		// create a new cookie with the right login details
		cookieLogic.addUserCookie(userName, hashedPassword);

		// destroy session
		requestLogic.invalidateSession();

		log.debug("redirect to root");
		// redirect to home
		return new ExtendedRedirectView("/");
	}

	@Override
	public Errors getErrors() {
		return this.errors;
	}

	@Override
	public void setErrors(Errors errors) {
		this.errors = errors;
	}

	@Override
	public void setRequestLogic(RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

	@Override
	public void setCookieLogic(CookieLogic cookieLogic) {
		this.cookieLogic = cookieLogic;
	}

	@Override
	public PasswordChangeOnRemindCommand instantiateCommand() {
		return new PasswordChangeOnRemindCommand();
	}

	@Override
	public Validator<PasswordChangeOnRemindCommand> getValidator() {
		return new PasswordChangeOnRemindValidator();
	}

	@Override
	public boolean isValidationRequired(PasswordChangeOnRemindCommand command) {
		return true;
	}

	/**
	 * @param adminLogic
	 */
	public void setAdminLogic(LogicInterface adminLogic){
		this.adminLogic = adminLogic;
	}

}
