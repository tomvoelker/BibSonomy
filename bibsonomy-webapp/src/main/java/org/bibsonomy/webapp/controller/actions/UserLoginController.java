package org.bibsonomy.webapp.controller.actions;

import java.util.Calendar;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.webapp.command.actions.UserLoginCommand;
import org.bibsonomy.webapp.exceptions.ServiceUnavailableException;
import org.bibsonomy.webapp.util.CookieAware;
import org.bibsonomy.webapp.util.CookieLogic;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestAware;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.TeerGrube;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.validation.UserLoginValidator;
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
public class UserLoginController implements MinimalisticController<UserLoginCommand>, ErrorAware, ValidationAwareController<UserLoginCommand>, RequestAware, CookieAware {
	private static final Logger log = Logger.getLogger(UserLoginController.class);

	protected LogicInterface adminLogic;
	private Errors errors = null;
	private RequestLogic requestLogic;
	private CookieLogic cookieLogic;
	
	/**
	 * The max number of minutes, a password reminder is valid.
	 */
	private int maxMinutesPasswordReminderValid = 60;

	private TeerGrube grube;


	/** 
	 * Returns an instance of the command the controller handles.
	 * 
	 * @see org.bibsonomy.webapp.util.MinimalisticController#instantiateCommand()
	 */
	public UserLoginCommand instantiateCommand() {
		return new UserLoginCommand();
	}


	/** Main method which does the registration.
	 * 
	 * @see org.bibsonomy.webapp.util.MinimalisticController#workOn(java.lang.Object)
	 */
	public View workOn(UserLoginCommand command) {
		log.debug("workOn() called");

		/*
		 * TODO: may logged in users use this page? 
		 */
		
		command.setPageTitle("login");
		
		
		/*
		 * remember referer to send user back to the page he's coming from
		 */
		if (!ValidationUtils.present(command.getReferer())) {
			command.setReferer(requestLogic.getReferer());
			log.debug("Set referer to " + command.getReferer());
		}
		

		/* Check cookies
		 * 
		 * Check, if user has cookies enabled (there should be at least a "JSESSIONID" cookie)
		 */
		if (!cookieLogic.containsCookies()) {
			errors.reject("error.cookies_required");
		}


		if (errors.hasErrors()) {
			return Views.LOGIN;
		}

		final String username = command.getUsername();
		final String password = command.getPassword();
		final String hashedPassword = StringUtils.getMD5Hash(password);



		/*
		 * Get the hosts IP address.
		 * 
		 * Since we're typically behind a proxy, we have to strip the proxies address.
		 * TODO: Does stripping the proxy work?
		 * 
		 */
		final String inetAddress = requestLogic.getInetAddress();


		final long remainingWaitSecondsIP   = grube.getRemainingWaitSeconds(inetAddress);
		final long remainingWaitSecondsName = grube.getRemainingWaitSeconds(username);
		/*
		 * take the maximum
		 */
		final long waitingSeconds = (remainingWaitSecondsIP > remainingWaitSecondsName ? remainingWaitSecondsIP : remainingWaitSecondsName);
		/*
		 * check in how many seconds the user is allowed to use this service 
		 */
		if (waitingSeconds > 5) {
			/*
			 * either ip or user name is blocked for more than 5 seconds from now --> log and send error page 
			 */
			log.warn("user " + username + " from IP " + inetAddress + " tried to login but still has to wait for max(" 
					+ remainingWaitSecondsName + ", " + remainingWaitSecondsIP + ") = " + waitingSeconds + " seconds.");

			/*
			 * Send user error message.
			 */
			throw new ServiceUnavailableException("error.service_unavailable", waitingSeconds);
		}


		/*
		 * checking password of user
		 */
		final User user = adminLogic.getUserDetails(username);
		if (!hashedPassword.equals(user.getPassword())) {
			/*
			 * passwords do not match -> check password from reminder
			 */
			if (!password.equals(user.getReminderPassword())) {
				/*
				 * password does neither match real or reminder password --> show error message
				 */
				log.info("Login of user " + username + " failed.");
				errors.reject("error.login.failed");
				/*
				 * count failures
				 */
				grube.add(username);
				grube.add(inetAddress);
			} else {
				/*
				 * passwords do match -> check if reminder still valid or already expired.
				 */
				final Calendar now = Calendar.getInstance();
				/*
				 * set expiration date by adding the max number of minutes a password
				 * reminder is valid to the time where the user requested the reminder
				 */
				final Calendar reminderExpirationDate = Calendar.getInstance();
				reminderExpirationDate.setTime(user.getReminderPasswordRequestDate());
				reminderExpirationDate.add(Calendar.MINUTE, maxMinutesPasswordReminderValid);

				if (now.after(reminderExpirationDate)) {
					/*
					 * reminder expired
					 */
					log.info("Login of user " + username + " failed, because of expired password reminder.");
					errors.reject("error.login.reminderExpired");
				} else {
					/*
					 * reminder password correct (and used!) and reminder not expired. Send 
					 * user to password change page.
					 * 
					 * FIXME: migration of password_change neccessary! Using the session is
					 * not the best idea - find better solutions! Maybe a separate password-
					 * change page is the best, where the user enters his temporary password,
					 * and his new password (twice).
					 */
					requestLogic.setSessionAttribute("tmpUser", username); // FIXME: remove this!
					return new ExtendedRedirectView("/change_password");
				}
			}
		}


		/*
		 * on error, send user back
		 */
		if (errors.hasErrors()) {
			return Views.LOGIN;
		}


		/*
		 * user successfully authenticated!
		 */


		/*
		 * add authentication cookie to response
		 */
		cookieLogic.addUserCookie(username, hashedPassword);

		/*
		 * flag spammers with a cookie
		 */
		cookieLogic.addSpammerCookie(user.isSpammer());

		/*
		 * To prevent Session-Fixation attacks (see http://www.jsptutorial.org/content/session) 
		 * we invalidate the old session.
		 */
		requestLogic.invalidateSession();


		/*
		 * redirect to referer or home page 
		 * FIXME: if user is coming from /login, he's send back to login!
		 */

		if (ValidationUtils.present(command.getReferer())) {
			return new ExtendedRedirectView(command.getReferer());
		}
		return new ExtendedRedirectView("/");
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
	public boolean isValidationRequired(final UserLoginCommand command) {
		/*
		 * TODO: When is validation really required?
		 */
		return true;
	}

	public Validator<UserLoginCommand> getValidator() {
		return new UserLoginValidator();
	}

	/**
	 * @see org.bibsonomy.webapp.util.RequestAware#setRequestLogic(org.bibsonomy.webapp.util.RequestLogic)
	 */
	public void setRequestLogic(RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

	/**
	 * Set an instance of the {@link TeerGrube}. This is used to store waiting times for 
	 * IPs/users which failed too often to enter the correct username.
	 * 
	 * @param grube
	 */
	@Required
	public void setGrube(TeerGrube grube) {
		this.grube = grube;
	}

	/** Set the admin logic required by this controller to check the credentials of the user.
	 * @param adminLogic
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
	 * The maximal number of minutes, a password reminder is valid.
	 * 
	 * @param maxMinutesPasswordReminderValid
	 */
	public void setMaxMinutesPasswordReminderValid(int maxMinutesPasswordReminderValid) {
		this.maxMinutesPasswordReminderValid = maxMinutesPasswordReminderValid;
	}


	/**
	 * @param cookieLogic
	 */
	public void setCookieLogic(CookieLogic cookieLogic) {
		this.cookieLogic = cookieLogic;
	}

}
