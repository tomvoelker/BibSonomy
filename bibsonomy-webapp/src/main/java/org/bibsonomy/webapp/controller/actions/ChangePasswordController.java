package org.bibsonomy.webapp.controller.actions;

import static org.bibsonomy.util.ValidationUtils.present;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.UserUpdateOperation;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.util.UserUtils;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.util.spring.security.UserAdapter;
import org.bibsonomy.webapp.command.SettingsViewCommand;
import org.bibsonomy.webapp.config.AuthConfig;
import org.bibsonomy.webapp.config.AuthMethod;
import org.bibsonomy.webapp.util.CookieAware;
import org.bibsonomy.webapp.util.CookieLogic;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.spring.security.rememberMeServices.CookieBasedRememberMeServices;
import org.bibsonomy.webapp.validation.ChangePasswordValidator;
import org.bibsonomy.webapp.view.Views;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.Errors;

/**
 * @author cvo
 * @version $Id$
 */
public class ChangePasswordController implements ValidationAwareController<SettingsViewCommand>, ErrorAware, CookieAware {
	private static final Log log = LogFactory.getLog(ChangePasswordController.class);

	/**
	 * hold current errors
	 */
	private Errors errors = null;

	/**
	 * logic interface
	 */
	private LogicInterface adminLogic = null;
	
	/**
	 * to update the user password cookie
	 */
	private CookieLogic cookieLogic;
	
	/**
	 * determines whether internal authentication (and thus password change) is enabled
	 */
	private AuthConfig authConfig;
	
	private CookieBasedRememberMeServices rememberMeServices;

	/**
	 * @param rememberMeServices the rememberMeServices to set
	 */
	public void setRememberMeServices(CookieBasedRememberMeServices rememberMeServices) {
		this.rememberMeServices = rememberMeServices;
	}

	@Override
	public SettingsViewCommand instantiateCommand() {
		return new SettingsViewCommand();
	}

	@Override
	public View workOn(final SettingsViewCommand command) {
		/*
		 * throw an exception if internal authentication is not available and 
		 * someone tries to change his password 
		 */
		if (!(present(this.authConfig) && this.authConfig.containsAuthMethod(AuthMethod.INTERNAL.name())) ) {
			throw new RuntimeException("Changing the password is not possible."); 
		}
		
		final RequestWrapperContext context = command.getContext();
	
		/*
		 * user has to be logged in to change his password
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

		/*
		 * LDAP and OpenID users can't change their password.
		 */
		if (present(loginUser.getLdapId())) {
			/*
			 * user exists and e-mail-address is fine but user has an LDAP ID
			 * and thus shall not use the reminder
			 */
			errors.reject("error.settings.password.ldap", "You are logged in using LDAP and thus don't have a password you could change.");
		} else if (present(loginUser.getOpenID())) {
			/*
			 * user exists and e-mail-address is fine but user has an OpenID
			 * and thus shall not use the reminder
			 */
			errors.reject("error.settings.password.openid", "You are logged in using OpenID and thus don't have a password you could change.");
		}
		
		/*
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
			this.changePassword(loginUser, command.getOldPassword(), command.getNewPassword());
		} else {
			errors.reject("error.field.valid.ckey");
		}

		return Views.SETTINGSPAGE;
	}

	private void changePassword(final User loginUser, final String oldPassword, final String newPassword) {
		/*
		 * first, check given current password
		 */
		if (loginUser.getPassword().equals(StringUtils.getMD5Hash(oldPassword))) {
			/*
			 * compute hash for new password
			 */
			final String newPasswordHash = StringUtils.getMD5Hash(newPassword);
			loginUser.setPassword(newPasswordHash);

			/*
			 * update password of user
			 */
			final String updatedUser = this.adminLogic.updateUser(loginUser, UserUpdateOperation.UPDATE_PASSWORD);
			
			/*
			 * change the cookie
			 */
			final UserDetails userDetails = new UserAdapter(loginUser);
			final Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, newPasswordHash);

			/*
			 * FIXME: This does currently not work, because the rememberMeService 
			 * sets the cookie only when the corresponding request parameter is
			 * supplied. Since we can't change request parameters, we probably
			 * have to add a checkbox to the password change form
			 */
			this.cookieLogic.updateRememberMeCookie(this.rememberMeServices, authentication);
			log.debug("password of " + updatedUser + " has been changed successfully");
		} else {
			// old password is wrong
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
	 * sets the admin logic interface
	 * 
	 * @param adminLogic
	 */
	public void setAdminLogic(final LogicInterface adminLogic) {
		this.adminLogic = adminLogic;
	}

	@Override
	public Validator<SettingsViewCommand> getValidator() {
		return new ChangePasswordValidator();
	}

	@Override
	public boolean isValidationRequired(final SettingsViewCommand command) {
		return true;
	}

	/**
	 * @param cookieLogic the cookieLogic to set
	 */
	@Override
	public void setCookieLogic(CookieLogic cookieLogic) {
		this.cookieLogic = cookieLogic;
	}

	/**
	 * @param authConfig
	 */
	public void setAuthConfig(AuthConfig authConfig) {
		this.authConfig = authConfig;
	}

}
