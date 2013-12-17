package org.bibsonomy.webapp.util.spring.security.exceptions;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;

/**
 * Signals that a user that was successfully authenticated using SAML was not
 * found in the database. 
 * 
 * @author jensi
  */
public class SamlUsernameNotFoundException extends UsernameNotFoundException {
	private static final long serialVersionUID = 317691533775866307L;
	
	private final SAMLCredential samlCreds;
	
	/**
	 * @param msg
	 * @param samlCreds credentials as received in the assertion from the idp
	 */
	public SamlUsernameNotFoundException(String msg, SAMLCredential samlCreds) {
		super(msg);
		this.samlCreds = samlCreds;
	}
	
	/**
	 * {@link #SamlUsernameNotFoundException(String, SAMLCredential)} but with standard message
	 * @param samlCreds credentials as received in the assertion from the idp
	 */
	public SamlUsernameNotFoundException(SAMLCredential samlCreds) {
		this("SAML id not found in database", samlCreds);
	}

	/**
	 * @return the credentials from the SAML assertion message
	 */
	public SAMLCredential getSamlCreds() {
		return this.samlCreds;
	}

}
