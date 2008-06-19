package org.bibsonomy.webapp.util.captcha;

/**
 * @author rja
 * @version $Id$
 */
public interface CaptchaResponse {

	
	/**
	 * 
	 * @return <code>true</code>, if the response matched the challenge.
	 */
	public boolean isValid();
	
	/**
	 * @return An error messages describing error occured during checking the response.
	 */
	public String getErrorMessage();
}
