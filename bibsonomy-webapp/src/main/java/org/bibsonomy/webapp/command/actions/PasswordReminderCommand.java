package org.bibsonomy.webapp.command.actions;



import java.io.Serializable;

import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.BaseCommand;

/**
 * @author daill
 * @version $Id$
 */
public class PasswordReminderCommand extends BaseCommand implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2980720178626094002L;
	
	/**
	 * A User object with the necessary information like email and nickname
	 */
	private User requestedUser;
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
	 * @return returns the requestedUser Object
	 */
	public User getRequestedUser() {
		return this.requestedUser;
	}
	/**
	 * @param requestedUser
	 */
	public void setRequestedUser(User requestedUser) {
		this.requestedUser = requestedUser;
	}
	/**
	 * @return returns a captcha
	 */
	public String getCaptchaHTML() {
		return this.captchaHTML;
	}
	
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
}
