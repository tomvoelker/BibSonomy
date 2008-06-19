package org.bibsonomy.webapp.util.captcha;

import java.util.Locale;

/**
 * @author rja
 * @version $Id$
 */
public interface Captcha {

	/** Creates the HTML string which displays the captcha.
	 * 
	 * @param locale - to determine the language for the captcha description.
	 * @return A piece of HTML code rendering the Captcha. 
	 */
	public String createCaptchaHtml(final Locale locale);
	
	/** Checks the response corresponding to the challenge.
	 * 
	 * @param challenge
	 * @param response
	 * @param remoteHostInetAddress
	 * @return A response containing errors and information about the validity.
	 */
	public CaptchaResponse checkAnswer(final String challenge, final String response, final String remoteHostInetAddress);
	
}
