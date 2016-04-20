package org.bibsonomy.webapp.util.spring.security.saml.credential.checker;

import org.springframework.security.saml.SAMLCredential;

/**
 * default implementation for {@link SAMLCredentialChecker}
 * checks nothing
 *
 * @author dzo
 */
public class DefaultSAMLCredentialChecker implements SAMLCredentialChecker {

	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.util.spring.security.saml.credential.checker.SAMLCredentialChecker#checkCredential(org.springframework.security.saml.SAMLCredential)
	 */
	@Override
	public void checkCredential(SAMLCredential credential) {
		// noop
	}

}
