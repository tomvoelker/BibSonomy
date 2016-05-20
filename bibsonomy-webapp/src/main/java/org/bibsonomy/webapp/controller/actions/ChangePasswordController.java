/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.controller.actions;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.AuthMethod;
import org.bibsonomy.common.enums.UserUpdateOperation;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.UserUtils;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.util.spring.security.UserAdapter;
import org.bibsonomy.webapp.command.SettingsViewCommand;
import org.bibsonomy.webapp.controller.SettingsPageController;
import org.bibsonomy.webapp.util.CookieAware;
import org.bibsonomy.webapp.util.CookieLogic;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.spring.security.rememberMeServices.CookieBasedRememberMeServices;
import org.bibsonomy.webapp.validation.ChangePasswordValidator;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author cvo
 */
public class ChangePasswordController extends SettingsPageController implements ValidationAwareController<SettingsViewCommand>, CookieAware {
	private static final Log log = LogFactory.getLog(ChangePasswordController.class);
	
	/**
	 * to update the user password cookie
	 */
	private CookieLogic cookieLogic;
	
	/**
	 * determines whether internal authentication (and thus password change) is enabled
	 */
	private List<AuthMethod> authConfig;

	private CookieBasedRememberMeServices rememberMeServices;

	@Override
	public View workOn(final SettingsViewCommand command) {
		/*
		 * throw an exception if internal authentication is not available and 
		 * someone tries to change his password 
		 */
		if (!this.authConfig.contains(AuthMethod.INTERNAL)) {
			throw new RuntimeException("Changing the password is not possible.");
		}
		
		final RequestWrapperContext context = command.getContext();
	
		/*
		 * user has to be logged in to change his password
		 */
		if (!context.isUserLoggedIn()) {
			throw new AccessDeniedException("please log in");
		}
		
		final User loginUser = context.getLoginUser();

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
		} else if (loginUser.getRemoteUserIds().size() > 0) {
			/*
			 * user exists and e-mail-address is fine but user has a remoteId
			 * and thus shall not use the reminder
			 */
			errors.reject("error.settings.password.sso", "You are logged in using a single-sign-on service and thus don't have a password you could change.");
		}
		
		/*
		 * go back to the settings page and display errors from command field
		 * validation
		 */
		if (errors.hasErrors() || ! HttpMethod.POST.equals(requestLogic.getHttpMethod())) {
			return super.workOn(command);
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

		return super.workOn(command);
	}

	private void changePassword(final User loginUser, final String oldPassword, final String newPassword) {
		/*
		 * first, check given current password
		 */
		if (loginUser.getPassword().equals(UserUtils.getPassword(oldPassword, loginUser.getPasswordSalt()))) {
			/*
			 * hash and salt password
			 */
			UserUtils.setupPassword(loginUser, newPassword);

			/*
			 * update password of user
			 */
			final String updatedUser = this.logic.updateUser(loginUser, UserUpdateOperation.UPDATE_PASSWORD);
			
			/*
			 * change the cookie
			 */
			final UserDetails userDetails = new UserAdapter(loginUser);
			final Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, loginUser.getPassword());
			/*
			 * rememberMeService sets the cookie only when the corresponding request parameter is
			 * supplied. Since we can't change request parameters, we added a hidden field to the
			 * password change form
			 */
			this.cookieLogic.updateRememberMeCookie(this.rememberMeServices, authentication);
			log.debug("password of " + updatedUser + " has been changed successfully");
		} else {
			// old password is wrong
			errors.rejectValue("oldPassword", "error.settings.password.incorect");
		}
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
	 * @param rememberMeServices the rememberMeServices to set
	 */
	public void setRememberMeServices(final CookieBasedRememberMeServices rememberMeServices) {
		this.rememberMeServices = rememberMeServices;
	}
	
	/**
	 * @param authConfig the authConfig to set
	 */
	public void setAuthConfig(final List<AuthMethod> authConfig) {
		this.authConfig = authConfig;
	}

	/**
	 * @param cookieLogic the cookieLogic to set
	 */
	@Override
	public void setCookieLogic(final CookieLogic cookieLogic) {
		this.cookieLogic = cookieLogic;
	}
}
