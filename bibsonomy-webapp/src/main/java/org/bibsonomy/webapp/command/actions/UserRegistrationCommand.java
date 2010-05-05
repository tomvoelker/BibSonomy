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

	/**
	 * 
	 */
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

	public String getCaptchaHTML() {
		return this.captchaHTML;
	}

	public void setCaptchaHTML(String captchaHTML) {
		this.captchaHTML = captchaHTML;
	}

	public String getRecaptcha_challenge_field() {
		return this.recaptcha_challenge_field;
	}

	public void setRecaptcha_challenge_field(String recaptcha_challenge_field) {
		this.recaptcha_challenge_field = recaptcha_challenge_field;
	}

	public String getRecaptcha_response_field() {
		return this.recaptcha_response_field;
	}

	public void setRecaptcha_response_field(String recaptcha_response_field) {
		this.recaptcha_response_field = recaptcha_response_field;
	}

	public String getPasswordCheck() {
		return this.passwordCheck;
	}

	public void setPasswordCheck(String passwordCheck) {
		this.passwordCheck = passwordCheck;
	}

	public boolean isAcceptPrivacy() {
		return this.acceptPrivacy;
	}
	public boolean getAcceptPrivacy() {
		return this.acceptPrivacy;
	}
	public void setAcceptPrivacy(boolean acceptPrivacy) {
		this.acceptPrivacy = acceptPrivacy;
	}

}
