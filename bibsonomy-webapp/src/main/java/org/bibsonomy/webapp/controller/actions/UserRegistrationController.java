package org.bibsonomy.webapp.controller.actions;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.Properties;

import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaResponse;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.InetAddressStatus;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.util.MailUtils;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.webapp.command.actions.UserRegistrationCommand;
import org.bibsonomy.webapp.util.CookieAware;
import org.bibsonomy.webapp.util.CookieLogic;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestAware;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.validation.UserRegistrationValidator;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

/** This controller handles the registration of users.
 * 
 * @author rja
 * @version $Id$
 */
public class UserRegistrationController implements MinimalisticController<UserRegistrationCommand>, ErrorAware, ValidationAwareController<UserRegistrationCommand>, RequestAware, CookieAware {
	
	/**
	 * After successful registration, the user is redirected to this page. 
	 */
	private String successRedirect = "/actions/register/user_success";

	private static final Logger log = Logger.getLogger(UserRegistrationController.class);

	protected LogicInterface logic;
	protected LogicInterface adminLogic;
	private Errors errors = null;
	private ReCaptcha reCaptcha;
	private RequestLogic requestLogic;
	private CookieLogic cookieLogic;

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
		Assert.notNull(adminLogic, "The provided logic interface must not be null.");
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
		final Locale locale = requestLogic.getLocale();
		final RequestWrapperContext context = command.getContext();
		final User loginUser = context.getLoginUser();

		/* Check user role
		 * 
		 * If user is logged in and not an admin: show error message
		 */
		if (context.isUserLoggedIn() && !Role.ADMIN.equals(loginUser.getRole())) {
			log.warn("User " + loginUser.getName() + " tried to access user registration without having role " + Role.ADMIN);
			throw new ValidationException("error.method_not_allowed");
		}

		/* Check cookies
		 * 
		 * Check, if user has cookies enabled (there should be at least a "JSESSIONID" cookie)
		 */
		if (!cookieLogic.containsCookies()) {
			errors.reject("error.cookies_required");
		}

		/*
		 * User which wants to register (form data)
		 */
		final User registerUser = command.getRegisterUser();


		/*
		 * Get the hosts IP address.
		 */
		final String inetAddress = requestLogic.getInetAddress();
		final String hostInetAddress = requestLogic.getHostInetAddress();


		/* Check spamIP
		 * 
		 * TODO: This is a canidate of functionality for a super-class "CaptchaController".
		 * 
		 * check, if IP is blocked from registration or
		 *        if user has spammer cookie set
		 * if one of the conditions is true, the user is silently blocked and has to re-enter
		 * the captcha again and again.
		 */
		if (InetAddressStatus.WRITEBLOCKED.equals(getInetAddressStatus(hostInetAddress)) || cookieLogic.hasSpammerCookie()) {
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
					throw new InternServerException("error.captcha");
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
		 * hash password of user before storing it into database
		 */
		registerUser.setPassword(StringUtils.getMD5Hash(registerUser.getPassword()));
		
		/*
		 * create user in DB
		 */
		logic.createUser(registerUser);

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
		 * log user into system and present him a success view
		 */
		cookieLogic.addUserCookie(registerUser.getName(), registerUser.getPassword());
		return new ExtendedRedirectView(successRedirect);
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
	@Required
	public void setReCaptcha(ReCaptcha reCaptcha) {
		this.reCaptcha = reCaptcha;
	}

	/** The logic needed to access the request
	 * @param requestLogic 
	 */
	@Required
	public void setRequestLogic(RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

	/** The logic needed to access the cookies.
	 * 
	 * @param cookieLogic
	 */
	@Required
	public void setCookieLogic(CookieLogic cookieLogic) {
		this.cookieLogic = cookieLogic;
	}

	/** After successful registration, the user is redirected to this page.
	 * @param successRedirect
	 */
	public void setSuccessRedirect(String successRedirect) {
		this.successRedirect = successRedirect;
	}

}
