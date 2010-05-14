package org.bibsonomy.webapp.controller.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.UserUpdateOperation;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.util.UserUtils;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.webapp.command.SettingsViewCommand;
import org.bibsonomy.webapp.util.CookieLogic;
import org.bibsonomy.webapp.util.ErrorAware;
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
 * @version $Id$
 */
public class ChangePasswordController implements ValidationAwareController<SettingsViewCommand>, ErrorAware {

	private static final Log log = LogFactory.getLog(ChangePasswordController.class);

	private static final String TAB_URL = "/settings";

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
		command.setTabURL(TAB_URL);
		return command;
	}

	@Override
	public View workOn(final SettingsViewCommand command) {
		final RequestWrapperContext context = command.getContext();
	
		/*
		 * user has to be logged in to delete himself
		 */
		if (!context.isUserLoggedIn()) {
			errors.reject("error.general.login");
			return Views.SETTINGSPAGE;
		}
		
		final User loginUser = context.getLoginUser();
		command.setUser(loginUser);
		
		/*
		 * check whether the user is a group		
		 */
		if (UserUtils.userIsGroup(loginUser)) {
			command.setHasOwnGroup(true);
			command.showGroupTab(true);
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
			/*
			 * change password
			 */
			this.changePassword(loginUser, command);
		} else {
			errors.reject("error.field.valid.ckey");
		}

		return Views.SETTINGSPAGE;
	}

	private void changePassword(final User loginUser, final SettingsViewCommand command) {

		/*
		 * first, check given current password
		 */
		if (loginUser.getPassword().equals(StringUtils.getMD5Hash(command.getOldPassword()))) {
			/*
			 * compute hash for new password
			 */
			final String newPasswordHash = StringUtils.getMD5Hash(command.getNewPassword());
			loginUser.setPassword(newPasswordHash);

			/*
			 * update password of user
			 */
			final String updatedUser = adminLogic.updateUser(loginUser, UserUpdateOperation.UPDATE_PASSWORD);
			
			/*
			 * set a new cookie
			 */
			cookieLogic.addUserCookie(loginUser.getName(), newPasswordHash);

			requestLogic.invalidateSession();

			log.debug("password of " + updatedUser + " has been changed successfully");
			
		} else {// old password is wrong
			errors.rejectValue("oldPassword", "error.settings.password.incorect");
		}
	}

	@Override
	public Errors getErrors() {
		return this.errors;
	}

	@Override
	public void setErrors(final Errors errors) {
		this.errors = errors;
	}

	/**
	 * sets the adming logic interface
	 * 
	 * @param adminLogic
	 */
	public void setAdminLogic(final LogicInterface adminLogic) {
		this.adminLogic = adminLogic;
	}

	/**
	 * sets the cookie logic interface
	 * 
	 * @param cookieLogic
	 */
	public void setCookieLogic(final CookieLogic cookieLogic) {
		this.cookieLogic = cookieLogic;
	}

	/**
	 * sets the request logic interface
	 * 
	 * @param requestLogic
	 */
	public void setRequestLogic(final RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

	@Override
	public Validator<SettingsViewCommand> getValidator() {
		return new ChangePasswordValidator();
	}

	@Override
	public boolean isValidationRequired(final SettingsViewCommand command) {
		return true;
	}
}
