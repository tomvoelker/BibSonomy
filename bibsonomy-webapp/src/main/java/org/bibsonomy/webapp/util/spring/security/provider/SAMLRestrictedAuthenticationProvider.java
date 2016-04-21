package org.bibsonomy.webapp.util.spring.security.provider;

import org.bibsonomy.webapp.util.spring.security.saml.credential.checker.SAMLCredentialChecker;
import org.springframework.security.saml.SAMLAuthenticationProvider;
import org.springframework.security.saml.SAMLCredential;

/**
 * {@link SAMLAuthenticationProvider} that prechecks saml credentials before
 * loading the userdetails
 *
 * @author dzo
 */
public class SAMLRestrictedAuthenticationProvider extends SAMLAuthenticationProvider {
	
	private SAMLCredentialChecker preSAMLChecker;
	
	/* (non-Javadoc)
	 * @see org.springframework.security.saml.SAMLAuthenticationProvider#getUserDetails(org.springframework.security.saml.SAMLCredential)
	 */
	@Override
	protected Object getUserDetails(SAMLCredential credential) {
		this.preSAMLChecker.checkCredential(credential);
		return super.getUserDetails(credential);
	}

	/**
	 * @param preSAMLChecker the preSAMLChecker to set
	 */
	public void setPreSAMLChecker(SAMLCredentialChecker preSAMLChecker) {
		this.preSAMLChecker = preSAMLChecker;
	}
}
