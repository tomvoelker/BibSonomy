package org.bibsonomy.webapp.util.captcha;

import net.tanesha.recaptcha.ReCaptchaResponse;

/** Wrapper around {@link ReCaptchaResponse}
 * @author rja
 * @version $Id$
 */
public class ReCaptchaResponseWrapper implements CaptchaResponse {
	
	private final ReCaptchaResponse response;
	
	/**
	 * @param response A response from a ReCaptchaImplementation.
	 */
	public ReCaptchaResponseWrapper(final ReCaptchaResponse response) {
		this.response = response;
	}

	@Override
	public String getErrorMessage() {
		return response.getErrorMessage();
	}

	@Override
	public boolean isValid() {
		return response.isValid();
	}

}
