package org.bibsonomy.webapp.controller.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.SettingPageMsg;
import org.bibsonomy.common.enums.UserUpdateOperation;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.webapp.command.actions.ChangePasswordCommand;
import org.bibsonomy.webapp.controller.SearchPageController;
import org.bibsonomy.webapp.util.CookieLogic;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * @author cvo
 * @version $Id$
 */
public class ChangePasswordController  implements
		MinimalisticController<ChangePasswordCommand>, ErrorAware {

	private static final Log log = LogFactory
			.getLog(SearchPageController.class);

	/**
	 * hold current errors
	 */
	private Errors errors = null;

	/**
	 * logic interface
	 */
	private LogicInterface adminLogic = null;
	
	/**
	 * cookie logic
	 */
	private CookieLogic cookieLogic = null;
	
	/**
	 * request logic interface
	 */
	private RequestLogic requestLogic;

	@Override
	public ChangePasswordCommand instantiateCommand() {
		final ChangePasswordCommand command = new ChangePasswordCommand();
		return command;
	}

	@Override
	public View workOn(ChangePasswordCommand command) {
		
		RequestWrapperContext context = command.getContext();
		
		Integer statusID = null;
		
		String referer = "";
		
		/*
		 * user has to be logged in to delete himself
		 */
		if (!context.isUserLoggedIn()){
			errors.reject("error.general.login");
		}
		
		/*
		 * check the ckey
		 */
		if (context.isValidCkey() && !errors.hasErrors()){
			log.debug("User is logged in, ckey is valid");
			
			//change password here
			System.out.println("password can be changed here");
			statusID = changePassword(context.getLoginUser(), command);
			
		} else {
			errors.reject("error.field.valid.ckey");
		}
		
		
		if(statusID != null) {
			referer = "/settingsnew?selTab=1" + "&statusID=" + statusID;
		}else{ // error occurred
			errors.reject("error.invalid_parameter");
		}
		
		/*
		 * if there are errors, show them
		 */
		if (errors.hasErrors()){
			return Views.ERROR;
		}
		
		return new ExtendedRedirectView(referer);
	}
	
	private int changePassword(User user, ChangePasswordCommand command) {
		
		String curPassword = user.getPassword();
		
		Integer msgID = null;
		
		// create the md5 hash of the new password
		final String hashedOldPassword = StringUtils.getMD5Hash(command.getOldPassword());
		
		if(curPassword.equals(hashedOldPassword)) {
					
			//new password was typed identical twice
			if(command.getNewPassword().equals(command.getNewPasswordRetype())) {
			
				String newPasswordHash = StringUtils.getMD5Hash(command.getNewPassword());
								
				user.setPassword(newPasswordHash);
				
				adminLogic.updateUser(user, UserUpdateOperation.UPDATE_PASSWORD);
				
				cookieLogic.addUserCookie(user.getName(), newPasswordHash);
				
				requestLogic.invalidateSession();
				
				msgID = SettingPageMsg.PASSWORD_CHANGED_SUCCESS.getId();
			}else {
				msgID = SettingPageMsg.PASSWORD_RETYPE_ERROR.getId();
			}
		}else {// old password is wrong
			
			msgID = SettingPageMsg.PASSWORD_CURRENT_ERROR.getId();
		}	
		
		return msgID;
	}

	@Override
	public Errors getErrors() {
		
		return this.errors;
	}

	@Override
	public void setErrors(Errors errors) {
		
		this.errors = errors;		
	}

	/**
	 * sets the adming logic interface
	 * @param adminLogic
	 */
	public void setAdminLogic(LogicInterface adminLogic) {
		this.adminLogic = adminLogic;
	}

	/**
	 * sets the cookie logic interface
	 * @param cookieLogic
	 */
	public void setCookieLogic(CookieLogic cookieLogic) {
		this.cookieLogic = cookieLogic;
	}

	/**
	 * sets the request logic interface
	 * @param requestLogic
	 */
	public void setRequestLogic(RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}
}
