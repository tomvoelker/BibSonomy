package org.bibsonomy.webapp.controller.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.SettingPageMsg;
import org.bibsonomy.common.enums.UserUpdateOperation;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.webapp.command.SettingsViewCommand;
import org.bibsonomy.webapp.controller.SearchPageController;
import org.bibsonomy.webapp.util.CookieLogic;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.validation.ChangePasswordValidator;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * @author cvo
 * @version $Id: ChangePasswordController.java,v 1.4 2009-12-02 12:51:14
 *          voigtmannc Exp $
 */
public class ChangePasswordController implements MinimalisticController<SettingsViewCommand>, ErrorAware, ValidationAwareController<SettingsViewCommand> {

	private static final Log log = LogFactory.getLog(SearchPageController.class);

	private static final String TAB_URL = "/settingsnew";

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
	public SettingsViewCommand instantiateCommand() {
		final SettingsViewCommand command = new SettingsViewCommand();
		command.setGroup(GroupUtils.getPublicGroup().getName());
		command.setTabURL(TAB_URL);
		return command;
	}

	@Override
	public View workOn(SettingsViewCommand command) {

		RequestWrapperContext context = command.getContext();
	
		/*
		 * user has to be logged in to delete himself
		 */
		if (!context.isUserLoggedIn()) {
			errors.reject("error.general.login");
			return Views.SETTINGSPAGE;
		} else {
			command.setUser(context.getLoginUser());
		}

		/**
		 * go back to the settings page and display errors from command field
		 * validation
		 */
		if (errors.hasErrors()) {
			return Views.SETTINGSPAGE;
		}

		/*
		 * check the ckey
		 */
		if (context.isValidCkey()) {
			log.debug("User is logged in, ckey is valid");

			// change password here
			System.out.println("password can be changed here");
			command.setStatusID(changePassword(context.getLoginUser(), command));

		} else {
			errors.reject("error.field.valid.ckey");
		}


		return Views.SETTINGSPAGE;
	}

	private int changePassword(User user, SettingsViewCommand command) {

		String curPassword = user.getPassword();
		int msgID;
		// create the md5 hash of the new password
		final String hashedOldPassword = StringUtils.getMD5Hash(command.getOldPassword());

		if (curPassword.equals(hashedOldPassword)) {

			String newPasswordHash = StringUtils.getMD5Hash(command.getNewPassword());

			user.setPassword(newPasswordHash);

			String updatedUser = adminLogic.updateUser(user, UserUpdateOperation.UPDATE_PASSWORD);

			cookieLogic.addUserCookie(user.getName(), newPasswordHash);

			requestLogic.invalidateSession();

			log.info("password of " + updatedUser + " has been changed successfully");
			
			msgID = SettingPageMsg.PASSWORD_CHANGED_SUCCESS.getId();
			
		} else {// old password is wrong
			msgID = SettingPageMsg.IDLE.getId();
			errors.reject("error.settings.password.incorect");			
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
	 * 
	 * @param adminLogic
	 */
	public void setAdminLogic(LogicInterface adminLogic) {
		this.adminLogic = adminLogic;
	}

	/**
	 * sets the cookie logic interface
	 * 
	 * @param cookieLogic
	 */
	public void setCookieLogic(CookieLogic cookieLogic) {
		this.cookieLogic = cookieLogic;
	}

	/**
	 * sets the request logic interface
	 * 
	 * @param requestLogic
	 */
	public void setRequestLogic(RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

	@Override
	public Validator<SettingsViewCommand> getValidator() {

		return new ChangePasswordValidator();
	}

	@Override
	public boolean isValidationRequired(SettingsViewCommand command) {

		return true;
	}
}
