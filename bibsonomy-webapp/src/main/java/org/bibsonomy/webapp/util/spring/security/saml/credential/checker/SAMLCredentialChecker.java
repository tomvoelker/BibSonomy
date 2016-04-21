package org.bibsonomy.webapp.util.spring.security.saml.credential.checker;

import org.springframework.security.saml.SAMLCredential;

/**
 * checker for saml credentials
 * 
 * @author dzo
 */
public interface SAMLCredentialChecker {

	/**
	 * @param credential
	 */
	public void checkCredential(SAMLCredential credential);

}
