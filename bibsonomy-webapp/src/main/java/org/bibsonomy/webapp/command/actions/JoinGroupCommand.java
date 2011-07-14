package org.bibsonomy.webapp.command.actions;

import org.bibsonomy.model.Group;
import org.bibsonomy.webapp.command.BaseCommand;


/**
 * @author schwass
 * @version $Id$
 */
public class JoinGroupCommand extends BaseCommand implements CaptchaCommand {
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
	private Group groupObj;
	
	private final int reasonMaxLen;
	
	/**
	 * @param reasonMaxLen
	 */
	public JoinGroupCommand(final int reasonMaxLen) {
		this.reasonMaxLen = reasonMaxLen;
	}
	
	/**
	 * user to be denied for joining group
	 */
	private String deniedUser;
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.command.actions.RecaptchaCommand#getRecaptcha_challenge_field()
	 */
	@Override
	public String getRecaptcha_challenge_field() {
		return this.recaptcha_challenge_field;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.command.actions.RecaptchaCommand#setRecaptcha_challenge_field(java.lang.String)
	 */
	@Override
	public void setRecaptcha_challenge_field(final String recaptchaChallengeField) {
		this.recaptcha_challenge_field = recaptchaChallengeField;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.command.actions.RecaptchaCommand#getRecaptcha_response_field()
	 */
	@Override
	public String getRecaptcha_response_field() {
		return this.recaptcha_response_field;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.command.actions.RecaptchaCommand#setRecaptcha_response_field(java.lang.String)
	 */
	@Override
	public void setRecaptcha_response_field(final String recaptchaResponseField) {
		this.recaptcha_response_field = recaptchaResponseField;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.command.actions.RecaptchaCommand#setCaptchaHTML(java.lang.String)
	 */
	@Override
	public void setCaptchaHTML(final String captchaHTML) {
		this.captchaHTML = captchaHTML;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.command.actions.RecaptchaCommand#getCaptchaHTML()
	 */
	@Override
	public String getCaptchaHTML() {
		return captchaHTML;
	}

	/**
	 * @param reason
	 */
	public void setReason(final String reason) {
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
	public void setGroup(final String group) {
		this.group = group;
	}

	/**
	 * @return group
	 */
	public String getGroup() {
		return group;
	}

	/**
	 * @param groupObj
	 */
	public void setGroupObj(final Group groupObj) {
		this.groupObj = groupObj;
	}

	/**
	 * @return group object
	 */
	public Group getGroupObj() {
		return groupObj;
	}

	/**
	 * @param deniedUser
	 */
	public void setDeniedUser(final String deniedUser) {
		this.deniedUser = deniedUser;
	}

	/**
	 * @return denied user
	 */
	public String getDeniedUser() {
		return deniedUser;
	}

	/**
	 * @return the reasonMaxLen
	 */
	public int getReasonMaxLen() {
		return reasonMaxLen;
	}


}
