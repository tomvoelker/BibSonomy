package org.bibsonomy.webapp.util.captcha;

import java.util.Locale;

/**
 * @author rja
  */
public class MockCaptcha implements Captcha {

	@Override
	public CaptchaResponse checkAnswer(final String challenge, final String response, final String hostInetAddress) {
		return new MockCaptchaResponse();
	}

	@Override
	public String createCaptchaHtml(final Locale locale) {
		return "";
	}

}
