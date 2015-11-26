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

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import net.tanesha.recaptcha.ReCaptcha;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.AuthMethod;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.enums.UserUpdateOperation;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.util.UserUtils;
import org.bibsonomy.util.HashUtils;
import org.bibsonomy.util.MailUtils;
import org.bibsonomy.util.UrlUtils;
import org.bibsonomy.webapp.command.actions.PasswordReminderCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.RequestAware;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.captcha.Captcha;
import org.bibsonomy.webapp.util.captcha.CaptchaUtil;
import org.bibsonomy.webapp.validation.PasswordReminderValidator;
import org.bibsonomy.webapp.view.Views;
import org.jasypt.util.text.BasicTextEncryptor;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

/**
 * @author daill
 */
public class PasswordReminderController implements ErrorAware, ValidationAwareController<PasswordReminderCommand>, RequestAware {
	private static final Log log = LogFactory.getLog(PasswordReminderController.class);
	
	private int maxMinutesPasswordReminderValid = 60;
	private LogicInterface adminLogic;
	private Errors errors = null;
	private RequestLogic requestLogic;
	private Captcha captcha;
	private MailUtils mailUtils;
	private String cryptKey;
	private List<AuthMethod> authConfig;
	
	@Override
	public PasswordReminderCommand instantiateCommand() {
		return new PasswordReminderCommand();
	}
	
	@Override
	public View workOn(final PasswordReminderCommand command) {
		if (command.getContext().isUserLoggedIn()) {
			throw new AccessDeniedException("only not loggedin users can request a reminder password");
		}
		/*
		 * check if internal authentication is supported
		 */
		if (!this.authConfig.contains(AuthMethod.INTERNAL)) {
			errors.reject("error.method_not_allowed");
			return Views.ERROR;
		}
		
		// get locale
		final Locale locale = requestLogic.getLocale();
		
		final User user = new User();
		user.setName(command.getUserName());
		user.setEmail(command.getUserEmail());

		/*
		 * Get the hosts IP address.
		 */
		final String inetAddress = requestLogic.getInetAddress();
		final String hostInetAddress = requestLogic.getHostInetAddress();


		/*
		 * check captcha
		 */
		CaptchaUtil.checkCaptcha(this.captcha, this.errors, log, command.getRecaptcha_challenge_field(), command.getRecaptcha_response_field(), hostInetAddress);

		/*
		 * If the user name is null, we get an exception on getUserDetails.
		 * Hence, we send the user back to the form.
		 */
		if (errors.hasErrors()) {
			/*
			 * Generate HTML to show captcha.
			 */
			command.setCaptchaHTML(captcha.createCaptchaHtml(locale));
			return Views.PASSWORD_REMINDER;
		}

		/*
		 * check, if user name exists
		 */
		final User existingUser = adminLogic.getUserDetails(user.getName());
		
		/*
		 * note. we have to check here also the deleted role because we use
		 * a admin logic to get the user details
		 */
		if (!UserUtils.isExistingUser(existingUser) || Role.LIMITED.equals(existingUser.getRole()) || !user.getEmail().equalsIgnoreCase(existingUser.getEmail())) {
			/*
			 * user does not exist or has been deleted (we should not sent 
			 * reminders to deleted users!)
			 */
			errors.rejectValue("userName", "error.passReminder.invalidUsernameOrMail");
		} else if (present(existingUser.getLdapId())) {
			/*
			 * user exists and e-mail-address is fine but user has an LDAP ID
			 * and thus shall not use the reminder
			 */
			errors.reject("error.passReminder.ldap", "You are registered using LDAP and thus don't have a password we could send you a reminder for.");
		} else if (existingUser.getRemoteUserIds().size() > 0) {
			/*
			 * user exists and e-mail-address is fine but user has a remoteId
			 * and thus shall not use the reminder
			 */
			errors.reject("error.passReminder.sso", "You are registered using a single-sign-on service and thus don't have a password we could send you a reminder for.");
		}

		/*
		 * If the user does not exist, getReminderPasswordRequestDate() returns null.
		 * Hence, we send the user back to the form.
		 */
		if (errors.hasErrors()) {
			/*
			 * Generate HTML to show captcha.
			 */
			command.setCaptchaHTML(captcha.createCaptchaHtml(locale));
			return Views.PASSWORD_REMINDER;
		}

		/*
		 * check, if user has requested a password reminder not so long ago
		 */
		final Calendar now = Calendar.getInstance();
		/*
		 * set expiration date by adding the max number of minutes a password
		 * reminder is valid to the time where the user requested the reminder
		 */
		final Calendar reminderExpirationDate = Calendar.getInstance();
		reminderExpirationDate.setTime(existingUser.getReminderPasswordRequestDate());
		reminderExpirationDate.add(Calendar.MINUTE, maxMinutesPasswordReminderValid);
		if (now.before(reminderExpirationDate)) {
			/*
			 * existing reminder still valid
			 */
			final int waitingMinutes = (int) (reminderExpirationDate.getTimeInMillis() - now.getTimeInMillis()) / 1000 / 60;
			errors.reject("error.passReminder.time", 
					new Object[]{maxMinutesPasswordReminderValid, waitingMinutes}, 
					"You already requested a password in the last " + maxMinutesPasswordReminderValid + " minutes. Please wait " + waitingMinutes + " minutes before you can request a new password");
		}

		/*
		 * Password reminder still valid -> send user back.
		 */
		if (errors.hasErrors()) {
			/*
			 * Generate HTML to show captcha.
			 */
			command.setCaptchaHTML(captcha.createCaptchaHtml(locale));
			return Views.PASSWORD_REMINDER;
		}

		/*
		 * at this point the given information like email and username are correct, and now we
		 * need to create a new pass and put it into the DB and send it per mail.
		 */

		/*
		 * check if user acknowledged the deletion of his OpenID access
		 */
		if (present(existingUser.getOpenID())){
			if(!command.isAcknowledgeOpenIDDeletion()){
				errors.rejectValue("acknowledgeOpenIDDeletion", "error.field.value.acknowledge");
			}
			if (errors.hasErrors()) {
				command.setCaptchaHTML(captcha.createCaptchaHtml(locale));
				return Views.PASSWORD_REMINDER;
			}
			this.adminLogic.updateUser(user, UserUpdateOperation.DELETE_OPENID);
		}
			
		/*
		 * create the random pw and set it to the user object
		 */
		final String tempPassword = getRandomString();
		user.setReminderPassword(tempPassword);
		user.setReminderPasswordRequestDate(new Date());
		
		// create reminder hash
		final String reminderHash = this.encryptReminderHash(user.getName(), tempPassword);

		// update db and delete OpenID Access if there is any
		adminLogic.updateUser(user, UserUpdateOperation.UPDATE_ALL);
		
		// send mail
		mailUtils.sendPasswordReminderMail(user.getName(), user.getEmail(), inetAddress, locale, maxMinutesPasswordReminderValid, UrlUtils.safeURIEncode(reminderHash));

		command.setSuccess(true);
		return Views.PASSWORD_REMINDER;
	}

	@Override
	public Errors getErrors() {
		return errors;
	}

	@Override
	public void setErrors(final Errors errors) {
		this.errors = errors;
	}

	@Override
	public Validator<PasswordReminderCommand> getValidator() {
		return new PasswordReminderValidator();
	}

	@Override
	public boolean isValidationRequired(final PasswordReminderCommand command) {
		return true;
	}
	
	@Required
	@Override
	public void setRequestLogic(final RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

	/** Give this controller an instance of {@link ReCaptcha}.
	 * 
	 * @param captcha 
	 */
	@Required
	public void setCaptcha(final Captcha captcha) {
		this.captcha = captcha;
	}
	
	/**
	 * @param adminLogic - an instance of the logic interface with admin access.
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
	 * Creates the random string
	 * 
	 * @return String
	 */
	private String getRandomString() {
		final Random rand = new Random();
		final byte[] bytes = new byte[8];
		rand.nextBytes(bytes);
		return HashUtils.toHexString(bytes);
	}

	/**
	 * @param mailUtils
	 */
	public void setMailUtils(final MailUtils mailUtils) {
		this.mailUtils = mailUtils;
	}

	/** The maximal number of minutes, a password reminder is valid.
	 * 
	 * @param maxMinutesPasswordReminderValid
	 */
	public void setMaxMinutesPasswordReminderValid(final int maxMinutesPasswordReminderValid) {
		this.maxMinutesPasswordReminderValid = maxMinutesPasswordReminderValid;
	}

	/**
	 * encode the reminder hash
	 * @param username - the username
	 * @param tempPassword - the temporary password
	 * @return the encrypted
	 */
	private String encryptReminderHash(final String username, final String tempPassword) {
		final BasicTextEncryptor crypt = new BasicTextEncryptor();
		crypt.setPassword(this.cryptKey);
		final String reminderCred = username + ":" + tempPassword;
		return crypt.encrypt(reminderCred);
	}

	/**
	 * Sets the key to encrypt the password reminder
	 * 
	 * @param cryptKey
	 */
	public void setCryptKey(final String cryptKey) {
		this.cryptKey = cryptKey;
	}

	/**
	 * @param authConfig the authConfig to set
	 */
	public void setAuthConfig(final List<AuthMethod> authConfig) {
		this.authConfig = authConfig;
	}
	
}
