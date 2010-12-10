package org.bibsonomy.webapp.command.actions;



import java.io.Serializable;

import org.bibsonomy.webapp.command.BaseCommand;

/**
 * Command for sending password reminder emails.
 * 
 * @author daill
 * @version $Id$
 */
public class PasswordReminderCommand extends BaseCommand implements Serializable{

	private static final long serialVersionUID = 6971611795826344738L;

	/**
	 * The user's name.
	 */
	private String userName;
	
	/**
	 * The user's email address.
	 */
	private String userEmail;
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
	 * true when password reminder has been sent successfully
	 */
	private boolean success = false;

	/**
	 * @return returns a captcha
	 */
	public String getCaptchaHTML() {
		return this.captchaHTML;
	}
	
	//**********************************************************************
	// getter / setter
	//**********************************************************************	
	
	/**
	 * @param captchaHTML
	 */
	public void setCaptchaHTML(String captchaHTML) {
		this.captchaHTML = captchaHTML;
	}
	
	/**
	 * @return captcha
	 */
	public String getRecaptcha_challenge_field() {
		return this.recaptcha_challenge_field;
	}
	
	/**
	 * @param recaptcha_challenge_field
	 */
	public void setRecaptcha_challenge_field(String recaptcha_challenge_field) {
		this.recaptcha_challenge_field = recaptcha_challenge_field;
	}
	
	/**
	 * @return captcha entry
	 */
	public String getRecaptcha_response_field() {
		return this.recaptcha_response_field;
	}
	
	/**
	 * @param recaptcha_response_field
	 */
	public void setRecaptcha_response_field(String recaptcha_response_field) {
		this.recaptcha_response_field = recaptcha_response_field;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return this.userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the userEmail
	 */
	public String getUserEmail() {
		return this.userEmail;
	}

	/**
	 * @param userEmail the userEmail to set
	 */
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public boolean getSuccess() {
		return success;
	}
}
