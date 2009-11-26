package org.bibsonomy.webapp.controller.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.UserUpdateOperation;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.webapp.command.actions.PasswordChangeOnRemindCommand;
import org.bibsonomy.webapp.util.CookieAware;
import org.bibsonomy.webapp.util.CookieLogic;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
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
public class PasswordChangeOnRemindController implements MinimalisticController<PasswordChangeOnRemindCommand>, ErrorAware, ValidationAwareController<PasswordChangeOnRemindCommand>, RequestAware, CookieAware{
	private static final Log log = LogFactory.getLog(PasswordChangeOnRemindController.class);
	
	private LogicInterface adminLogic;
	private CookieLogic cookieLogic;
	private RequestLogic requestLogic;

	private Errors errors;
	
	public View workOn(PasswordChangeOnRemindCommand command) {
		log.debug("starting work");
		command.setPageTitle("Password change");
		
		// set the username and the tmp password
		command.setUserName((String)requestLogic.getSessionAttribute("tmpUser"));

		if (command.getUserName() != null){
			log.debug("neither username nor the tmppassword is null");
			
			// if there are any errors show thems
			if (errors.hasErrors()) {
				return Views.PASSWORD_CHANGE_ON_REMIND;
			}

			// get the existing user
			User user = new User();
			user.setName(command.getUserName());
			
			// create the md5 hash of the new password
			final String hashedPassword = StringUtils.getMD5Hash(command.getNewPassword());
			
			// add the new password to the user object
			user.setPassword(hashedPassword);
			
			log.debug("writing the new password to the database");
			// update user in database
			adminLogic.updateUser(user, UserUpdateOperation.UPDATE_ALL);
			
			log.debug("setting up new cookie");
			// create a new cookie with the right login details
			cookieLogic.addUserCookie(command.getUserName(), hashedPassword);
			
			// destroy session
			requestLogic.invalidateSession();
			
			log.debug("redirect to root");
			// redirect to home
			return new ExtendedRedirectView("/");
		}
		
		log.debug("either username or tmppassword is null - throwing error");
		errors.reject("error.method_not_allowed");
		return Views.ERROR;
	}
	
	public Errors getErrors() {
		return this.errors;
	}

	public void setErrors(Errors errors) {
		this.errors = errors;
	}

	public void setRequestLogic(RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

	public void setCookieLogic(CookieLogic cookieLogic) {
		this.cookieLogic = cookieLogic;
	}

	public PasswordChangeOnRemindCommand instantiateCommand() {
		return new PasswordChangeOnRemindCommand();
	}

	public Validator<PasswordChangeOnRemindCommand> getValidator() {
		return new PasswordChangeOnRemindValidator();
	}

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
