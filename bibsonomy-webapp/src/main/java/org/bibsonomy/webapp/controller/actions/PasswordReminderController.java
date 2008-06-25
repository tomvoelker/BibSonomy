package org.bibsonomy.webapp.controller.actions;




import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import net.tanesha.recaptcha.ReCaptcha;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.InetAddressStatus;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.util.MailUtils;
import org.bibsonomy.webapp.command.actions.PasswordReminderCommand;
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
import org.bibsonomy.webapp.util.captcha.Captcha;
import org.bibsonomy.webapp.util.captcha.CaptchaResponse;
import org.bibsonomy.webapp.validation.PasswordReminderValidator;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.MessageSource;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

import resources.Resource;

/**
 * @author daill
 * @version $Id$
 */
public class PasswordReminderController implements MinimalisticController<PasswordReminderCommand>, ErrorAware, ValidationAwareController<PasswordReminderCommand>, RequestAware, CookieAware{
	private static final Logger log = Logger.getLogger(PasswordReminderController.class);
	
	private static final int MAX_TIME_IN_MINUTES = 15; 
	
	private static final String success = "/login";
	
	protected LogicInterface logic;
	protected LogicInterface adminLogic;
	private Errors errors = null;
	private RequestLogic requestLogic;
	private Captcha captcha;
	private CookieLogic cookieLogic;
	private MailUtils mailUtils;
	private MessageSource messageSource;

	public PasswordReminderCommand instantiateCommand() {
		final PasswordReminderCommand command = new PasswordReminderCommand();
		
		//create a new user object
		command.setRequestedUser(new User());
		
		return command;
	}

	public View workOn(PasswordReminderCommand command) {
		// get localisation
		final Locale locale = requestLogic.getLocale();
		
		// set page title
		command.setPageTitle(messageSource.getMessage("navi.passReminder", null, locale));

		final RequestWrapperContext context = command.getContext();
		User requUser = command.getRequestedUser();
		
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
			log.warn("Host " + hostInetAddress + " with SPAMMER cookie/blocked IP tried to remind password as user " + requUser.getName());
			errors.rejectValue("recaptcha_response_field", "error.field.valid.captcha");
		} else {
			/*
			 * Valid user
			 * 
			 * check captcha
			 */
			checkCaptcha(command.getRecaptcha_challenge_field(), command.getRecaptcha_response_field(), hostInetAddress);
		}
		
		if (errors.hasErrors()){
			/*
			 * Generate HTML to show captcha.
			 */
			command.setCaptchaHTML(captcha.createCaptchaHtml(locale));
			return Views.PASSWORD_REMINDER;
		}
		
		/*
		 * if the user is not logged in, we need an instance of the logic interface
		 * with admin access 
		 */
		if (!context.isUserLoggedIn()) {
			this.logic = this.adminLogic;
		}
		
		final User existingUser = logic.getUserDetails(requUser.getName());
		
		if (existingUser.getName() == null) {
			errors.rejectValue("requestedUser.name", "error.field.valid.user.name");
			command.setCaptchaHTML(captcha.createCaptchaHtml(locale));
			return Views.PASSWORD_REMINDER;
		} else if (!requUser.getEmail().toLowerCase().equals(existingUser.getEmail().toLowerCase())) {
			errors.rejectValue("requestedUser.email", "error.field.valid.user.email");
			command.setCaptchaHTML(captcha.createCaptchaHtml(locale));
			return Views.PASSWORD_REMINDER;
		}
		
		Timestamp now = new Timestamp(new Date().getTime());
		
		if(!((now.getTime() - (MAX_TIME_IN_MINUTES * 60 * 1000)) > existingUser.getReminderPasswordRequestDate().getTime())){
			errors.reject("error.passReminder.time");
			command.setCaptchaHTML(captcha.createCaptchaHtml(locale));
			return Views.PASSWORD_REMINDER;
		}
		
		/*
		 * at this point the given information like email and username are correct, and now we
		 * need to create a new pass and put it into the DB and send it per mail.
		 */
		
		/*
		 * create the random pw and set it to the user object
		 */
		String tmppw = getRandomString();
		requUser.setReminderPassword(tmppw);
		requUser.setReminderPasswordRequestDate(now);
		
		// update db
		logic.updateUser(requUser);
		
		// send mail
		mailUtils.sendPasswordReminderMail(requUser.getName(), requUser.getEmail(), inetAddress, locale, MAX_TIME_IN_MINUTES, tmppw);
		
		return new ExtendedRedirectView(success);
	}

	public Errors getErrors() {
		return errors;
	}

	public void setErrors(Errors errors) {
		this.errors = errors;
	}

	public Validator<PasswordReminderCommand> getValidator() {
		return new PasswordReminderValidator();
	}

	public boolean isValidationRequired(PasswordReminderCommand command) {
		return true;
	}
	
	/**
	 * @param logic - an instance of the logic interface.
	 */
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}

	@Required
	public void setRequestLogic(RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}
	
	/** Give this controller an instance of {@link ReCaptcha}.
	 * 
	 * @param captcha 
	 */
	@Required
	public void setCaptcha(Captcha captcha) {
		this.captcha = captcha;
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
	 * Checks the captcha. If the response from the user does not match the captcha,
	 * an error is added. 
	 * 
	 * @param command - the command associated with this request.
	 * @param hostInetAddress - the address of the client
	 * @throws InternServerException - if checking the captcha was not possible due to 
	 * an exception. This could be caused by a non-rechable captcha-server. 
	 */
	private void checkCaptcha(final String challenge, final String response, final String hostInetAddress) throws InternServerException {
		if (org.bibsonomy.util.ValidationUtils.present(challenge) && org.bibsonomy.util.ValidationUtils.present(response)) {
			/*
			 * check captcha response
			 */
			try {
				final CaptchaResponse res = captcha.checkAnswer(challenge, response, hostInetAddress);

				if (!res.isValid()) {
					/*
					 * invalid response from user
					 */
					errors.rejectValue("recaptcha_response_field", "error.field.valid.captcha");
				} else if (res.getErrorMessage() != null) {
					/*
					 * valid response, but still an error
					 */
					log.warn("Could not validate captcha response: " + res.getErrorMessage());
				}
			} catch (final Exception e) {
				log.fatal("Could not validate captcha response.", e);
				throw new InternServerException("error.captcha");
			}
		}
	}
	
	/** The logic needed to access the cookies.
	 * 
	 * @param cookieLogic
	 */
	@Required
	public void setCookieLogic(CookieLogic cookieLogic) {
		this.cookieLogic = cookieLogic;
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
	
	/**
	 * Creates the random string
	 * 
	 * @return String
	 */
	private String getRandomString() {
		Random rand = new Random();
		byte[] bytes = new byte[8];
		rand.nextBytes(bytes);
		return Resource.toHexString(bytes);
	}
	
	/** A message source to format mail messages.
	 * @param messageSource
	 */
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	/**
	 * @param mailUtils
	 */
	public void setMailUtils(MailUtils mailUtils) {
		this.mailUtils = mailUtils;
	}
}
