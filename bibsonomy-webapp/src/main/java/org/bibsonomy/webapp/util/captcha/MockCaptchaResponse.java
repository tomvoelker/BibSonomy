package org.bibsonomy.webapp.util.captcha;

/**
 * Response which is always true.
 * 
 * @author rja
 * @version $Id$
 */
public class MockCaptchaResponse implements CaptchaResponse {

	@Override
	public String getErrorMessage() {
		return "No error occured.";
	}

	@Override
	public boolean isValid() {
		return true;
	}

}
