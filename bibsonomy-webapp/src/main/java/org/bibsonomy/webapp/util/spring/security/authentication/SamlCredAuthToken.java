package org.bibsonomy.webapp.util.spring.security.authentication;

import java.util.Collections;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.saml.SAMLCredential;

/**
 * @author jensi
 * @version $Id$
 */
public class SamlCredAuthToken extends AbstractAuthenticationToken {
	private static final long serialVersionUID = -6393689353234458932L;
	
	private final SAMLCredential samlCreds;
	
	/**
	 * @param samlCreds the credentials as received from the SAML response
	 */
	public SamlCredAuthToken(SAMLCredential samlCreds) {
		super(Collections.<GrantedAuthority>emptyList());
		this.samlCreds = samlCreds;
	}
	
	@Override
	public SAMLCredential getCredentials() {
		return samlCreds;
	}

	@Override
	public Object getPrincipal() {
		return null;
	}

}
