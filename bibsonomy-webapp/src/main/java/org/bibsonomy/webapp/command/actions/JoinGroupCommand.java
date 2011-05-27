package org.bibsonomy.webapp.command.actions;

import org.bibsonomy.webapp.command.BaseCommand;


/**
 * @author schwass
 * @version $Id$
 */
public class JoinGroupCommand extends BaseCommand {
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
	 * reason for user to join group
	 */
	private String reason;
	/**
	 * group user want join
	 */
	private String group;

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
	 * @param captchaHTML
	 */
	public void setCaptchaHTML(String captchaHTML) {
		this.captchaHTML = captchaHTML;
	}

	/**
	 * @return captcha html
	 */
	public String getCaptchaHTML() {
		return captchaHTML;
	}

	/**
	 * @param reason
	 */
	public void setReason(String reason) {
		this.reason = reason;
	}

	/**
	 * @return reason
	 */
	public String getReason() {
		return reason;
	}

	/**
	 * @param group
	 */
	public void setGroup(String group) {
		this.group = group;
	}

	/**
	 * @return group
	 */
	public String getGroup() {
		return group;
	}


}
