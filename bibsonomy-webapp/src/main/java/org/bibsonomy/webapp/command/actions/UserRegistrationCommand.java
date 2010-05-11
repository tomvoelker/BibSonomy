package org.bibsonomy.webapp.command.actions;

import java.io.Serializable;

import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.BaseCommand;

/** 
 * This command encapsulates the user and other details for the registration page. 
 * 
 * @author rja
 * @version $Id$
 */
public class UserRegistrationCommand extends BaseCommand implements Serializable {
	private static final long serialVersionUID = 1371638749968299277L;
	
	
	/**
	 * Holds the details of the user which wants to register (like name, email, password)
	 */
	private User registerUser;
	/**
	 * The user has to re-type the password, to ensure, that he did not make any typos.
	 */
	private String passwordCheck;
	/**
	 * Contains the HTML-Code to view the reCaptcha. Is filled ONLY by the controller!
	 * Any validator must check, that the user did not fill this field.
	 */
	private String captchaHTML;
	/**
	 * The (encoded) challenge the user has to solve. Is given as a request parameter by 
	 * the reCaptcha form.
	 */
	private String recaptcha_challenge_field;
	/**
	 * The response to the captcha, the user entered.
	 */
	private String recaptcha_response_field;
	/**
	 * User accepts our privacy statement and AGB.
	 */
	private boolean acceptPrivacy;

	/** 
	 * @return The user which tries to register.
	 */
	public User getRegisterUser() {
		return this.registerUser;
	}

	/**
	 * @param registerUser - the user which tries to register.
	 */
	public void setRegisterUser(User registerUser) {
		this.registerUser = registerUser;
	}
	
	/**
	 * @return the captchaHTML
	 */
	public String getCaptchaHTML() {
		return this.captchaHTML;
	}

	/**
	 * @param captchaHTML the captchaHTML to set
	 */
	public void setCaptchaHTML(String captchaHTML) {
		this.captchaHTML = captchaHTML;
	}

	/**
	 * @return the recaptcha_challenge_field
	 */
	public String getRecaptcha_challenge_field() {
		return this.recaptcha_challenge_field;
	}

	/**
	 * @param recaptchaChallengeField the recaptcha_challenge_field to set
	 */
	public void setRecaptcha_challenge_field(String recaptchaChallengeField) {
		this.recaptcha_challenge_field = recaptchaChallengeField;
	}

	/**
	 * @return the recaptcha_response_field
	 */
	public String getRecaptcha_response_field() {
		return this.recaptcha_response_field;
	}

	/**
	 * @param recaptchaResponseField the recaptcha_response_field to set
	 */
	public void setRecaptcha_response_field(String recaptchaResponseField) {
		this.recaptcha_response_field = recaptchaResponseField;
	}

	/**
	 * @return the passwordCheck
	 */
	public String getPasswordCheck() {
		return this.passwordCheck;
	}

	/**
	 * @param passwordCheck the passwordCheck to set
	 */
	public void setPasswordCheck(String passwordCheck) {
		this.passwordCheck = passwordCheck;
	}
	
	/**
	 * @return the acceptPrivacy
	 */
	public boolean isAcceptPrivacy() {
		return this.acceptPrivacy;
	}
	
	/**
	 * @return @see {@link #isAcceptPrivacy()}
	 */
	public boolean getAcceptPrivacy() {
		return this.acceptPrivacy;
	}

	/**
	 * @param acceptPrivacy the acceptPrivacy to set
	 */
	public void setAcceptPrivacy(boolean acceptPrivacy) {
		this.acceptPrivacy = acceptPrivacy;
	}

}
