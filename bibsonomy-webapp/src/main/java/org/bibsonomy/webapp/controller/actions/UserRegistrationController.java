package org.bibsonomy.webapp.controller.actions;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaResponse;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.InetAddressStatus;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.model.User;
import org.bibsonomy.model.UserSettings;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.util.MailUtils;
import org.bibsonomy.webapp.command.actions.UserRegistrationCommand;
import org.bibsonomy.webapp.util.CookieHelper;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestAware;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.validation.UserRegistrationValidator;
import org.bibsonomy.webapp.view.RedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.support.RequestContext;

/** This controller handles the registration of users.
 * 
 * @author rja
 * @version $Id$
 */
public class UserRegistrationController implements MinimalisticController<UserRegistrationCommand>, ErrorAware, ValidationAwareController<UserRegistrationCommand>, RequestAware {
	private static final Logger log = Logger.getLogger(UserRegistrationController.class);

	protected LogicInterface logic;
	protected LogicInterface adminLogic;
	protected UserSettings userSettings;
	private Errors errors = null;
	private ReCaptcha reCaptcha;
	private HttpServletRequest request;

	/**
	 * @param logic - an instance of the logic interface.
	 */
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}

	/**
	 * @param adminLogic - an instance of the logic interface with admin access.
	 */
	@Required
	public void setAdminLogic(LogicInterface adminLogic) {
		this.adminLogic = adminLogic;
		/*
		 * Check, if logic has admin access.
		 */
		Assert.isTrue(Role.ADMIN.equals(this.adminLogic.getAuthenticatedUser().getRole()), "The provided logic interface must have admin access.");
	}

	/** 
	 * Returns an instance of the command the controller handles.
	 * 
	 * @see org.bibsonomy.webapp.util.MinimalisticController#instantiateCommand()
	 */
	public UserRegistrationCommand instantiateCommand() {
		final UserRegistrationCommand userRegistrationCommand = new UserRegistrationCommand();
		/*
		 * add user to command
		 */
		userRegistrationCommand.setRegisterUser(new User());
		return userRegistrationCommand;
	}


	/** Main method which does the registration.
	 * 
	 * @see org.bibsonomy.webapp.util.MinimalisticController#workOn(java.lang.Object)
	 */
	public View workOn(UserRegistrationCommand command) {
		log.debug("workOn() called");

		command.setPageTitle("registration");

		/*
		 * variables used throughout the method
		 */
		final Locale locale = new RequestContext(request).getLocale();
		final RequestWrapperContext context = command.getContext();
		final User loginUser = context.getLoginUser();

		/* Check user role
		 * 
		 * If user is logged in and not an admin: show error message
		 */
		if (context.isUserLoggedIn() && !Role.ADMIN.equals(loginUser.getRole())) {
			log.warn("User " + loginUser.getName() + " tried to access user registration without having role " + Role.ADMIN);
			errors.reject("error.method_not_allowed");
			command.setError("error.method_not_allowed");

			return Views.ERROR;
		}

		/* Check cookies
		 * 
		 * Check, if user has cookies enabled (there should be at least a "JSESSIONID" cookie)
		 */
		if (!requestContainsCookies()) {
			errors.reject("error.cookies_required");
		}

		/*
		 * User which wants to register (form data)
		 */
		final User registerUser = command.getRegisterUser();


		/*
		 * Get the hosts IP address.
		 * 
		 * Since we're typically behind a proxy, we have to strip the proxies address.
		 * TODO: Does stripping the proxy work?
		 * 
		 */
		final String inetAddress = request.getHeader("x-forwarded-for");
		final String hostInetAddress = getHostInetAddress(inetAddress);


		/* Check spamIP
		 * 
		 * TODO: This is a canidate of functionality for a super-class "CaptchaController".
		 * 
		 * check, if IP is blocked from registration or
		 *        if user has spammer cookie set
		 * if one of the conditions is true, the user is silently blocked and has to re-enter
		 * the captcha again and again.
		 */
		if (InetAddressStatus.WRITEBLOCKED.equals(getInetAddressStatus(hostInetAddress)) || CookieHelper.hasSpammerCookie(request.getCookies())) {
			/*
			 * Spammer found!
			 * 
			 * Must enter captcha again (and again, and again ...)
			 */
			log.warn("Host " + hostInetAddress + " with SPAMMER cookie/blocked IP tried to register as user " + registerUser.getName());
			errors.rejectValue("recaptcha_response_field", "error.field.valid.captcha");
		} else {
			/*
			 * Valid user
			 * 
			 * check captcha
			 */
			final String challenge = command.getRecaptcha_challenge_field();
			final String response = command.getRecaptcha_response_field();

			if (org.bibsonomy.util.ValidationUtils.present(challenge) && org.bibsonomy.util.ValidationUtils.present(response)) {
				/*
				 * check captcha response
				 */
				try {
					final ReCaptchaResponse res = reCaptcha.checkAnswer(hostInetAddress, challenge, response);

					log.error("Error validating captcha response: " + res.getErrorMessage());

					if (!res.isValid()) {
						errors.rejectValue("recaptcha_response_field", "error.field.valid.captcha");
						errors.rejectValue("recaptcha_response_field", "error.field.valid.captcha2");
					}
				} catch (final Exception e) {
					log.fatal("Could not validate captcha response.", e);
					errors.reject("error.captcha");
					command.setError("error.captcha");
					return Views.ERROR;
				}
			}
		}

		/*
		 * If user is an admin, he must provide a valid ckey!
		 */
		final boolean adminAccess = context.isUserLoggedIn() && Role.ADMIN.equals(loginUser.getRole());
		if (adminAccess && !context.isValidCkey()) {
			errors.reject("error.field.valid.ckey");
		}

		/*
		 * return to form until validation passes
		 */
		if (errors.hasErrors()) {
			/*
			 * Generate HTML to show captcha.
			 */
			command.setReCaptchaHTML(createReCaptchaHtml(locale));
			return Views.REGISTER_USER;
		}

		/*
		 * check, if user name already exists
		 */
		final User existingUser = logic.getUserDetails(registerUser.getName());
		if (existingUser.getName() != null) {
			/*
			 * yes -> user must choose another name
			 */
			errors.rejectValue("registerUser.name", "error.field.duplicate.user.name");
			command.setReCaptchaHTML(createReCaptchaHtml(locale));
			return Views.REGISTER_USER;
		}


		log.debug("validation passed with " + errors.getErrorCount() + " errors, proceeding to access database");

		/*
		 * if the user is not logged in, we need an instance of the logic interface
		 * with admin access 
		 */
		if (!context.isUserLoggedIn()) {
			this.logic = this.adminLogic;
		}


		/*
		 * at this point:
		 * - the form is filled with correct values
		 * - the captcha is correct
		 * - the user has cookies enabled
		 * - the user seems to be not a spammer
		 * - the user is an admin (with valid ckey) or not logged in
		 * - the user name does not exist in the DB
		 * - we have an instance of the LogicInterface with admin access
		 */


		/*
		 * set the full inet address of the user
		 */
		registerUser.setIPAddress(inetAddress);

		/*
		 * create user in DB
		 */
		try {
			logic.createUser(registerUser);
		} catch (final Exception e) {
			command.setError("Could not register user: " + e); // FIXME: error code expected!
			return Views.ERROR;
		}

		/*
		 * send registration confirmation mail
		 */
		try {
			MailUtils.getInstance().sendRegistrationMail(registerUser.getName(), registerUser.getEmail(), inetAddress, locale);
		} catch (final Exception e) {
			log.error("Could not send registration confirmation mail for user " + registerUser.getName(), e);
		}
		
		/*
		 * proceed to the view
		 */

		if (adminAccess) {
			/*
			 * admin created a new user -> give feedback about successful creation of user
			 */
			command.setRegisterUser(logic.getUserDetails(registerUser.getName()));
			return Views.REGISTER_USER_SUCCESS_ADMIN; 
		} 
		/*
		 * TODO: log user into system and present him a success view
		 */
		/*
		 * TODO: migrate success view to Spring MVC
		 */
		return Views.REGISTER_USER_SUCCESS;




	}

	/** Creates the HTML string which displays the captcha.
	 * 
	 * @param locale - to determine the language for the captcha description.
	 * @return A HTML string for the captcha. 
	 */
	private String createReCaptchaHtml(final Locale locale) {
		final Properties props = new Properties();
		/*
		 * set language
		 */
		props.setProperty("lang", locale.getLanguage());

		return reCaptcha.createRecaptchaHtml(null, props);
	}

	/** Checks, if the request contains any cookies.
	 * 
	 * @return
	 */
	private boolean requestContainsCookies() {
		return request.getCookies() != null && request.getCookies().length > 0;
	}

	public Errors getErrors() {
		return errors;
	}

	public void setErrors(final Errors errors) {
		/*
		 * here: check for binding errors
		 */
		this.errors = errors;
	}

	/** Returns, if validation is required for the given command. On default,
	 * for all incoming data validation is required.
	 * 
	 * @see org.bibsonomy.webapp.util.ValidationAwareController#isValidationRequired(java.lang.Object)
	 */
	public boolean isValidationRequired(final UserRegistrationCommand command) {
		/*
		 * TODO: When is validation really required?
		 */
		return true;
	}

	/** 
	 * From a comma-separated list of inetAddresses, returns the last entry
	 * of the list.
	 * 
	 * @param inetAddress - a comma separated list of inetAddresses.
	 * @return
	 */
	private String getHostInetAddress (final String inetAddress) {
		if (inetAddress != null) {
			final int proxyStartPos = inetAddress.indexOf(",");
			if (log.isDebugEnabled()) log.debug("inetAddress = " + inetAddress + ", proxyStartPos = " + proxyStartPos);
			if (proxyStartPos > 0) { 
				return inetAddress.substring(0, proxyStartPos);
			}
			if (log.isDebugEnabled()) log.debug("inetAddress = " + inetAddress + " (cutted)");
			return inetAddress;
		}
		return "";
	}


	/** Checks the status of the given inetAddress in the DB
	 * @param inetAddress
	 * @return
	 */
	private InetAddressStatus getInetAddressStatus(final String inetAddress) {
		// query the DB for the status of address 
		try {
			return logic.getInetAddressStatus(InetAddress.getByName(inetAddress));
		} catch (final UnknownHostException e) {
			log.info("Could not check inetAddress " + inetAddress, e);
		}
		// fallback: unknown
		return InetAddressStatus.UNKNOWN;
	}


	public Validator<UserRegistrationCommand> getValidator() {
		return new UserRegistrationValidator();
	}

	/** Give this controller an instance of {@link ReCaptcha}.
	 * 
	 * @param reCaptcha
	 */
	public void setReCaptcha(ReCaptcha reCaptcha) {
		this.reCaptcha = reCaptcha;
	}

	/**
	 * @see org.bibsonomy.webapp.util.RequestAware#setRequest(javax.servlet.http.HttpServletRequest)
	 */
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

}
