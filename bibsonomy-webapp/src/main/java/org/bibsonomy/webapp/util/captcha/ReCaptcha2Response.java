package org.bibsonomy.webapp.util.captcha;

/**
 * TODO: add documentation to this class
 *
 * @author niebler
 */
public class ReCaptcha2Response implements CaptchaResponse {
	
	private final boolean isValid;
	private final String errorMessage;
	
	/**
	 * @param isValid
	 * @param errorMessage
	 */
	public ReCaptcha2Response(boolean isValid, String errorMessage) {
		this.isValid = isValid;
		this.errorMessage = errorMessage;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.util.captcha.CaptchaResponse#isValid()
	 */
	@Override
	public boolean isValid() {
		return this.isValid;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.util.captcha.CaptchaResponse#getErrorMessage()
	 */
	@Override
	public String getErrorMessage() {
		return this.errorMessage;
	}

}
