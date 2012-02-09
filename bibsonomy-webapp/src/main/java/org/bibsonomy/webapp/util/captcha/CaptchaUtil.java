package org.bibsonomy.webapp.util.captcha;

import org.apache.commons.logging.Log;
import org.bibsonomy.common.exceptions.InternServerException;
import org.springframework.validation.Errors;

/**
 * Methods to handle captchas.
 * 
 * @author rja
 * @version $Id$
 */
public class CaptchaUtil {

	/**
	 * Checks the captcha. If the response from the user does not match the captcha,
	 * an error is added. 
	 * 
	 * @param captcha 
	 * @param errors 
	 * @param log 
	 * @param challenge 
	 * @param response 
	 * @param hostInetAddress - the address of the client
	 * @throws InternServerException - if checking the captcha was not possible due to 
	 * an exception. This could be caused by a non-rechable captcha-server. 
	 */
	public static void checkCaptcha(final Captcha captcha, final Errors errors, final Log log, final String challenge, final String response, final String hostInetAddress) throws InternServerException {
		/*
		 * check captcha response
		 */
		try {
			final CaptchaResponse res = captcha.checkAnswer(challenge, response, hostInetAddress);

			if (!res.isValid()) {
				/*
				 * invalid response from user
				 */
				errors.rejectValue("recaptcha_response_field", "error.field.valid.captcha", "The provided security token is invalid.");
			} else if (res.getErrorMessage() != null) {
				/*
				 * valid response, but still an error
				 */
				log.warn("Could not validate captcha response: " + res.getErrorMessage());
			}
		} catch (final Exception e) {
			log.fatal("Could not validate captcha response.", e);
			throw new InternServerException("error.captcha");
		}
	}
	
}
