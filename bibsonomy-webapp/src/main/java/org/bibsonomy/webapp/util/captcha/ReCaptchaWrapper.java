package org.bibsonomy.webapp.util.captcha;

import java.util.Locale;
import java.util.Properties;

import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaImpl;

/** Wrapper around {@link ReCaptcha}.
 * 
 * @author rja
 * @version $Id$
 */
public class ReCaptchaWrapper implements Captcha {

	private final ReCaptchaImpl reCaptcha;
	
	/**
	 * Create a new instance of the CaptchaImplementation, which is a wrapper
	 * around {@link ReCaptcha}.
	 */
	public ReCaptchaWrapper() {
		this.reCaptcha = new ReCaptchaImpl();
	}
	
	@Override
	public CaptchaResponse checkAnswer(String challenge, String response, String remoteAddr) {
		return new ReCaptchaResponseWrapper(reCaptcha.checkAnswer(remoteAddr, challenge, response));
	}

	@Override
	public String createCaptchaHtml(final Locale locale) {
		final Properties props = new Properties();
		/*
		 * set language
		 */
		props.setProperty("lang", locale.getLanguage());
		return reCaptcha.createRecaptchaHtml(null, props);
	}

	/** Sets the private key used to authenticate to the reCaptcha server
	 * to check the challenge. 
	 * 
	 * @param privateKey
	 */
	public void setPrivateKey(String privateKey) {
		reCaptcha.setPrivateKey(privateKey);
	}

	/** Sets the public key for communication with the reCaptcha server.
	 * @param publicKey
	 */
	public void setPublicKey(String publicKey) {
		reCaptcha.setPublicKey(publicKey);
	}

	/** Sets the inet address of the reCaptcha Server.
	 * 
	 * @param recaptchaServer
	 */
	public void setRecaptchaServer(String recaptchaServer) {
		reCaptcha.setRecaptchaServer(recaptchaServer);
	}

	/** Set this to <code>true</code>, if you want reCaptcha to include
	 * HTML &lt;noscript&gt; tags.
	 * @param includeNoscript
	 */
	public void setIncludeNoscript(boolean includeNoscript) {
		reCaptcha.setIncludeNoscript(includeNoscript);
	}

}
