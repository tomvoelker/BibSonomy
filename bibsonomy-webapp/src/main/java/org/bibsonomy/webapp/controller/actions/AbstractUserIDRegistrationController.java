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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.user.remote.RemoteUserId;
import org.bibsonomy.webapp.command.actions.UserIDRegistrationCommand;
import org.bibsonomy.webapp.util.CookieAware;
import org.bibsonomy.webapp.util.CookieLogic;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.RequestAware;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.spring.security.exceptions.AccessDeniedNoticeException;
import org.bibsonomy.webapp.util.spring.security.handler.FailureHandler;
import org.bibsonomy.webapp.util.spring.security.rememberMeServices.CookieBasedRememberMeServices;
import org.bibsonomy.webapp.validation.UserValidator;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

/**
 * This controller handles the registration of users via generic ID providers,
 * e.g., OpenID or LDAP. 
 * 
 * 
 * @author rja
 * @param <R> 
 */
public abstract class AbstractUserIDRegistrationController<R> implements ErrorAware, ValidationAwareController<UserIDRegistrationCommand>, RequestAware, CookieAware {
	private static final Log log = LogFactory.getLog(AbstractUserIDRegistrationController.class);
	
	protected LogicInterface adminLogic;
	private Errors errors = null;
	private RequestLogic requestLogic;
	private CookieLogic cookieLogic;
	private CookieBasedRememberMeServices rememberMeServices;
	private AuthenticationManager authenticationManager;
	
	/**
	 * The page with the form that will be shown to the user for registration.
	 */
	private Views registrationFormView;
	
	/**
	 * After successful registration, the user is redirected to this page.
	 */
	private String successRedirect = "";

	/**
	 * Only users which were successfully authenticated using the ID provider 
	 * and whose ID does not exist in our database are allowed to use this
	 * controller.
	 * 
	 * @see org.bibsonomy.webapp.util.MinimalisticController#workOn(org.bibsonomy.webapp.command.ContextCommand)
	 */
	@Override
	public View workOn(final UserIDRegistrationCommand command) {
		log.debug("workOn() called");

		/*
		 * If the user is already logged in: show error message
		 */
		if (command.getContext().isUserLoggedIn()) {
			throw new AccessDeniedException("error.method_not_allowed");
		}

		/*
		 * If the user has been successfully authenticated by the ID provider 
		 * and he is not yet registered, his data is contained in the session.  
		 */
		final Object o = this.requestLogic.getSessionAttribute(FailureHandler.USER_TO_BE_REGISTERED);
		if (!present(o) || !(o instanceof User)) {
			/*
			 * user must first login.
			 */
			throw new AccessDeniedNoticeException("please log in", this.getLoginNotice());
		}

		/*
		 * user found in session - proceed with the registration 
		 */
		log.debug("got user from session");
		final User user = (User) o;

		setFixedValuesFromUser(command, user);
		/*
		 * 2 = user has not been on form, yet -> fill it with user data from ID provider
		 * 3 = user has seen the form and possibly changed data
		 */
		if (command.getStep() == 2) {
			log.debug("step 2: start registration");
			/*
			 * fill command with user data from ID provider
			 */
			command.setRegisterUser(user);
			/*
			 * generate a new user name if necessary
			 */
			if (!present(user.getName())) {
				user.setName(this.generateUserName(user));
			}
			/*
			 * ensure that we proceed to the next step
			 */
			command.setStep(3);
			/*
			 * return to form
			 */
			return this.registrationFormView;
		}
		
		log.debug("step 3: complete registration");
		
		/* 
		 * if there are any errors in the form, we return back to fix them.
		 */
		if (this.errors.hasErrors()) {
			log.info("an error occoured: " + this.errors.toString());
			return this.registrationFormView;
		}
		/*
		 * no errors: try to store user
		 */
		/*
		 * user that wants to register (form data)
		 */
		final User registerUser = command.getRegisterUser();
		/*
		 * check, if user name already exists
		 */
		if (present(registerUser.getName()) && present(this.adminLogic.getUserDetails(registerUser.getName()).getName())) {
			/*
			 * yes -> user must choose another name
			 */
			this.errors.rejectValue("registerUser.name", "error.field.duplicate.user.name");
		}
		/*
		 * return to form until validation passes
		 */
		if (this.errors.hasErrors()) {
			/*
			 * We add the ID since it is shown to the user in the form.
			 */
			this.setAuthentication(registerUser, user);
			return this.registrationFormView;
		}
		log.info("validation passed with " + this.errors.getErrorCount() + " errors, proceeding to access database");
		/*
		 * set the user's inet address
		 */
		registerUser.setIPAddress(this.requestLogic.getInetAddress());
		/*
		 * before we store the user, we must ensure that he contains the 
		 * credentials used to authenticate him
		 */
		this.setAuthentication(registerUser, user);
		/*
		 * create user in DB
		 */
		this.adminLogic.createUser(registerUser);
		/*
		 * delete user from session.
		 */
		this.requestLogic.setSessionAttribute(FailureHandler.USER_TO_BE_REGISTERED, null);
		
		final R additionalInfoFromSession = this.getAdditionalInfoFromSession();
		return logOn(user, additionalInfoFromSession);
	}
	
	@SuppressWarnings("unchecked")
	private R getAdditionalInfoFromSession() {
		final String sessionKey = this.getAddtionsInfoSessionKey();
		if (present(sessionKey)) {
			return (R) this.requestLogic.getSessionAttribute(sessionKey);
		}
		return null;
	}

	/**
	 * @return the session key for additional info
	 */
	protected String getAddtionsInfoSessionKey() {
		return null;
	}

	/**
	 * subclasses can set additional properties in the command object
	 * @param command
	 * @param user
	 */
	protected void setFixedValuesFromUser(UserIDRegistrationCommand command, User user) {
		// noop
	}

	/**
	 * log user into system and return success view
	 * 
	 * @param user
	 * @param additionalInfoFromSession 
	 * @return success view
	 */
	private View logOn(final User user, final R additionalInfoFromSession) {
		/*
		 * log user into system
		 * TODO: user correct? not registerUser? (maybe the user has changed his name)
		 */
		final Authentication authentication = this.getAuthentication(user, additionalInfoFromSession);

		final Authentication authenticated = this.authenticationManager.authenticate(authentication);
		SecurityContextHolder.getContext().setAuthentication(authenticated);
		if (cookieLogic != null) {
			this.cookieLogic.createRememberMeCookie(this.rememberMeServices, authenticated);
		}

		/*
		 * present the success view
		 */
		return new ExtendedRedirectView(this.successRedirect);
	}
	
	/**
	 * Before using this controller, the user must have been authenticated 
	 * using the ID provider. If the user is not authenticated, she is sent 
	 * to the login page where this notice is shown. 
	 * 
	 * @return The notice shown on the login page when the user must 
	 * authenticate before registration.
	 */
	protected abstract String getLoginNotice();
	
	/**
	 * After successful registration the user must be "logged in" using Spring
	 * Security. Therefore, we need an authentication that the corresponding 
	 * authentication manager and remememberMeService can use. This depends 
	 * very much on the type of ID used for authentication. 
	 * Typically, a new {@link UsernamePasswordAuthenticationToken} is returned. 
	 * 
	 * @param user
	 * @param additionalInfoFromSession 
	 * @return the authentication
	 */
	protected abstract Authentication getAuthentication(final User user, R additionalInfoFromSession);
	
	/**
	 * Before we store the user <code>registerUser</code> in the database, his
	 * authentication credentials (e.g., ID and password) must be set. You must
	 * provide a method which does this using the data from <code>user</code> - 
	 * the user returned from the ID provider. 
	 * 
	 * @param registerUser - the user that will be stored in the database
	 * @param user - the user we got from the ID provider
	 */
	protected abstract void setAuthentication(final User registerUser, final User user);

	/**
	 * Generates a user name using the user's real name and some other heuristics.
	 * Ensures that the user name doesn't exist, yet. 
	 * 
	 * @param user - the user for that we shall generate a new user name
	 * @return A user name that does not exist, yet.
	 */
	protected String generateUserName(final User user) {
		/*
		 * Find user name which does not exist yet in the database.
		 * 
		 * check if username is already used and try another
		 */
		String newName = cleanUserName(user.getRealname());
		int tryCount = 0;
		log.debug("try existence of username: " + newName);
		while ((newName.equalsIgnoreCase(this.adminLogic.getUserDetails(newName).getName())) && (tryCount < 101)) {
			try {
				if (tryCount == 0) {
					// try first character of forename concatenated with surname
					// bugs bunny => bbunny
					newName = cleanUserName(user.getRealname()).substring(0, 1).concat(newName);
				} else if (tryCount == 100) {
					// now use first character of fore- and first two characters of surename concatenated with user id 
					// bugs bunny => bbu01234567
					String remoteUserId = getRemoteUserId(user);
					newName = cleanUserName(newName.substring(0, 3).concat(remoteUserId));
				} else {
					// try first character of forename concatenated with surename concatenated with current number
					// bugs bunny => bbunnyX where X is between 1 and 9
					if (tryCount==1) {
						// add trycount to newName
						newName = cleanUserName(newName.concat(Integer.toString(tryCount)));
					} else { 
						// replace last two characters of string with trycount
						newName = cleanUserName(newName.substring(0, newName.length() - Integer.toString(tryCount-1).length()).concat(Integer.toString(tryCount)));
					}
				}
				log.debug("try existence of username: " + newName + " (" + tryCount + ")");
				tryCount++;
			} catch (final IndexOutOfBoundsException ex) {
				/*
				 * if some substring values are out of range, catch exception and use surname
				 */
				newName = cleanUserName(user.getRealname());
				tryCount = 99;
			}
		}
		return newName;
	}

	private String getRemoteUserId(final User user) {
		if (user.getLdapId() != null) {
			return user.getLdapId();
		}
		if (user.getOpenID() != null) {
			return user.getOpenID();
		}
		for (RemoteUserId rId : user.getRemoteUserIds()) {
			return rId.getSimpleId();
		}
		return null;
		/*
		 * FIXME: Should we not throw an exception in this case? 
		 */
	}
	
	private static String cleanUserName(final String name) {
		if (!present(name)) {
			return "";
		}
		
		return UserValidator.USERNAME_DISALLOWED_CHARACTERS_PATTERN.matcher(name).replaceAll("").toLowerCase();
	}

	@Override
	public final UserIDRegistrationCommand instantiateCommand() {
		final UserIDRegistrationCommand command = instantiateCommandInternal();
		/*
		 * add user to command
		 */
		command.setRegisterUser(new User());
		return command;
	}

	protected UserIDRegistrationCommand instantiateCommandInternal() {
		return new UserIDRegistrationCommand();
	}

	@Override
	public Errors getErrors() {
		return this.errors;
	}

	@Override
	public void setErrors(final Errors errors) {
		this.errors = errors;
	}

	@Override
	public boolean isValidationRequired(final UserIDRegistrationCommand command) {
		return true;
	}

	@Override
	public void setRequestLogic(final RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

	@Override
	public void setCookieLogic(final CookieLogic cookieLogic) {
		this.cookieLogic = cookieLogic;
	}

	/**
	 * @param adminLogic
	 *            - an instance of the logic interface with admin access.
	 */
	@Required
	public void setAdminLogic(final LogicInterface adminLogic) {
		Assert.notNull(adminLogic, "The provided logic interface must not be null.");
		this.adminLogic = adminLogic;
		/*
		 * Check, if logic has admin access.
		 */
		Assert.isTrue(Role.ADMIN.equals(this.adminLogic.getAuthenticatedUser().getRole()), "The provided logic interface must have admin access.");
	}


	/**
	 * After successful registration, the user is redirected to this page.
	 * 
	 * @param successRedirect
	 */
	public void setSuccessRedirect(final String successRedirect) {
		this.successRedirect = successRedirect;
	}

	/**
	 * @return The remember me service.
	 */
	public CookieBasedRememberMeServices getRememberMeServices() {
		return this.rememberMeServices;
	}

	/**
	 * @param rememberMeServices
	 */
	public void setRememberMeServices(final CookieBasedRememberMeServices rememberMeServices) {
		this.rememberMeServices = rememberMeServices;
	}
	
	/**
	 * Sets the authentication manager used to authenticate the user after 
	 * successful registration.
	 * 
	 * @param authenticationManager
	 */
	public void setAuthenticationManager(final AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	/**
	 * Sets the view used to present the user the registration form.
	 * 
	 * @param registrationFormView
	 */
	public void setRegistrationFormView(final Views registrationFormView) {
		this.registrationFormView = registrationFormView;
	}

	/**
	 * @return the requestLogic
	 */
	public RequestLogic getRequestLogic() {
		return this.requestLogic;
	}

	/**
	 * @return the authenticationManager
	 */
	public AuthenticationManager getAuthenticationManager() {
		return this.authenticationManager;
	}
}