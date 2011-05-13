package org.bibsonomy.webapp.command;

/**
 * @author schwass
 * @version $Id$
 */
public interface CaptchaResponseCommand {

	/**
	 * @return the challenge
	 */
	String getChallenge();
	/**
	 * @return the captcha response
	 */
	String getResponse();
}
