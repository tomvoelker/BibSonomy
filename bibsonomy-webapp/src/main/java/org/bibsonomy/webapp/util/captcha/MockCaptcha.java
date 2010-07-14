package org.bibsonomy.webapp.util.captcha;

import java.util.Locale;

/**
 * @author rja
 * @version $Id$
 */
public class MockCaptcha implements Captcha {

	@Override
	public CaptchaResponse checkAnswer(String challenge, String response, String hostInetAddress) {
		return new MockCaptchaResponse();
	}

	@Override
	public String createCaptchaHtml(Locale locale) {
		return "<input type='hidden' name='recaptcha_response_field' value='foo'/>" +
			   "<strong>Captcha will be always true due to use of " + MockCaptcha.class.getName() + ".</strong>";
		
	}

}
