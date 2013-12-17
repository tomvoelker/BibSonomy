package org.bibsonomy.webapp.util.spring.security.authentication;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

/**
 * @author dzo
  */
public class SessionAuthenticationToken extends AbstractAuthenticationToken {
	private static final long serialVersionUID = 1434519528252232694L;
	
	private final Object principal;

	private Object creds = "";
	
	/**
	 * builds a session authentication token
	 * 
	 * @param principal
	 * @param authorities
	 */
	public SessionAuthenticationToken(final Object principal, Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.principal = principal;
		setAuthenticated(true);
	}

	@Override
	public Object getCredentials() {
		return creds;
	}
	
	/**
	 * @param creds the user credentials to be set
	 */
	public void setCreds(Object creds) {
		this.creds = creds;
	}

	@Override
	public Object getPrincipal() {
		return this.principal;
	}

}
