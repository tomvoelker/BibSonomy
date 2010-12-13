package org.bibsonomy.webapp.controller.actions;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.UserUpdateOperation;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.webapp.command.actions.PasswordChangeOnRemindCommand;
import org.bibsonomy.webapp.exceptions.InvalidPasswordReminderException;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.RequestAware;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.validation.PasswordChangeOnRemindValidator;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.jasypt.util.text.BasicTextEncryptor;
import org.springframework.validation.Errors;

/**
 * This controller is responsible for setting a new password after a password reminder
 * email has been sent. 
 * 
 * @author daill
 * @author Dominik Benz, benz@cs.uni-kassel.de
 * @version $Id$
 */
public class PasswordChangeOnRemindController implements ErrorAware, ValidationAwareController<PasswordChangeOnRemindCommand>, RequestAware {
	private static final Log log = LogFactory.getLog(PasswordChangeOnRemindController.class);

	private LogicInterface adminLogic;
	private RequestLogic requestLogic;

	private String cryptKey;
	private Errors errors;
	
	private int maxMinutesPasswordReminderValid = 60;

	@Override
	public View workOn(PasswordChangeOnRemindCommand command) {
		log.debug("starting work");
		command.setPageTitle("password change");
		
		/*
		 * no reminder hash given -> 
		 */
		if (!present(command.getReminderHash())) {
			return Views.PASSWORD_CHANGE_ON_REMIND;
		}

		/*
		 * extract the reminder credentials form the reminder hash, fetch 
		 * corresponding details from DB 
		 */
		final ReminderCredentials cred;
		try {
			cred = this.decryptReminderHash(command.getReminderHash());
		} catch (InvalidPasswordReminderException ex) {
			errors.reject("error.method_not_allowed");
			return Views.ERROR;
		}
		
		if (! present(cred.username) || ! present(cred.reminderPassword)) {
			errors.reject("error.method_not_allowed");
			return Views.ERROR;
		}
		final User user = adminLogic.getUserDetails(cred.username);
				
		/*
		 * check if the reminderPassword is correct
		 */		
		if (! present(user.getReminderPassword()) || ! user.getReminderPassword().equals(cred.reminderPassword) ) {
			errors.reject("error.reminder_password_not_correct");
			return Views.ERROR;
		}
		
		/*
		 * check if the reminderPassword has expired
		 */
		if (this.hasExpired(user.getReminderPasswordRequestDate())) {
			errors.reject("error.reminder_password_expired");
			return Views.ERROR;			
		}


		/*
		 * set user name into command to show it in form field
		 */
		command.setUserName(cred.username);
		
		/*
		 * if there are any errors show them
		 */
		if (errors.hasErrors()) {
			return Views.PASSWORD_CHANGE_ON_REMIND;
		}

		/*
		 * set new password, reset old one 
		 */
		final String hashedPassword = StringUtils.getMD5Hash(command.getNewPassword());
		user.setPassword(hashedPassword);
		user.setReminderPassword("");

		log.debug("writing the new password to the database");
		/*
		 * update user in database
		 */
		adminLogic.updateUser(user, UserUpdateOperation.UPDATE_PASSWORD);

		// destroy session
		requestLogic.invalidateSession();

		log.debug("redirect to login page");
		// redirect to home
		return new ExtendedRedirectView("/login?notice=login.notice.password_changed");
	}

	@Override
	public Errors getErrors() {
		return this.errors;
	}

	@Override
	public void setErrors(Errors errors) {
		this.errors = errors;
	}

	@Override
	public void setRequestLogic(RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

	@Override
	public PasswordChangeOnRemindCommand instantiateCommand() {
		return new PasswordChangeOnRemindCommand();
	}

	@Override
	public Validator<PasswordChangeOnRemindCommand> getValidator() {
		return new PasswordChangeOnRemindValidator();
	}

	@Override
	public boolean isValidationRequired(PasswordChangeOnRemindCommand command) {
		return true;
	}

	/**
	 * @param adminLogic
	 */
	public void setAdminLogic(LogicInterface adminLogic){
		this.adminLogic = adminLogic;
	}
		
	/**
	 * decode the reminder hash.
	 * @param reminderHash - the reminder hash
	 * @return the reminder credentials
	 */
	private ReminderCredentials decryptReminderHash(final String reminderHash) {
		try {
			final BasicTextEncryptor crypt = new BasicTextEncryptor();
			crypt.setPassword(this.getCryptKey());
			final String reminderHashDecrypted = crypt.decrypt(reminderHash);
			final String[] parts = reminderHashDecrypted.split(":");
			return new ReminderCredentials(parts[0], parts[1]);
		}
		catch (IndexOutOfBoundsException ex) {
			throw new InvalidPasswordReminderException();
		}
		catch (EncryptionOperationNotPossibleException ex) {
			throw new InvalidPasswordReminderException();
		}
	}
	
	/**
	 * check if the password reminder date is still valid
	 * @param reminderPasswordDate - the reminder date
	 * @return true if still valid, false otherwise
	 */
	private boolean hasExpired(final Date reminderPasswordDate) {
		return ( System.currentTimeMillis() - reminderPasswordDate.getTime() > (this.maxMinutesPasswordReminderValid * 60 * 1000) );
	}
	
	/**
	 * Sets the key to decrypt the password reminder
	 * 
	 * @param cryptKey - the crypt key
	 */
	public void setCryptKey(String cryptKey) {
		this.cryptKey = cryptKey;
	}
	
	/**
	 * get the crypt key
	 * 
	 * @return - The key to decrypt the password reminder
	 */
	public String getCryptKey() {
		return cryptKey;
	}
	
	/** The maximal number of minutes, a password reminder is valid.
	 * 
	 * @param maxMinutesPasswordReminderValid
	 */
	public void setMaxMinutesPasswordReminderValid(final int maxMinutesPasswordReminderValid) {
		this.maxMinutesPasswordReminderValid = maxMinutesPasswordReminderValid;
	}


	/**
	 * small helper class to hold username and temp-password
	 */
	private class ReminderCredentials {
		public ReminderCredentials(final String username, final String reminderPassword) {
			this.username=username;
			this.reminderPassword=reminderPassword;
		}
		public String username;
		public String reminderPassword;
	}	
	
}

