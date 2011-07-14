package org.bibsonomy.webapp.command.actions;

/**
 * @author dzo
 * @version $Id$
 */
public interface CaptchaCommand {

	/**
	 * @return the recaptcha_challenge_field
	 */
	public String getRecaptcha_challenge_field();

	/**
	 * @param recaptchaChallengeField the recaptcha_challenge_field to set
	 */
	public void setRecaptcha_challenge_field(String recaptchaChallengeField);

	/**
	 * @return the recaptcha_response_field
	 */
	public String getRecaptcha_response_field();

	/**
	 * @param recaptchaResponseField the recaptcha_response_field to set
	 */
	public void setRecaptcha_response_field(String recaptchaResponseField);

	/**
	 * @param captchaHTML
	 */
	public void setCaptchaHTML(String captchaHTML);

	/**
	 * @return captcha html
	 */
	public String getCaptchaHTML();

}