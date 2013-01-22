package org.bibsonomy.util.spring.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * User Details for users that are only authenticated remotely but do not exist locally
 * 
 * @author jensi
 * @version $Id$
 */
public class RemoteOnlyUserDetails implements UserDetails {
	private static final long serialVersionUID = -7121668540134905067L;
	
	private final Object creds;

	/**
	 * @param user the user to adapt
	 */
	public RemoteOnlyUserDetails(Object creds) {
		this.creds = creds;
	}
	
	@Override
	public Collection<GrantedAuthority> getAuthorities() {
		return Collections.emptyList();
	}

	@Override
	public String getPassword() {
		return null;
	}

	@Override
	public String getUsername() {
		return null;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	/**
	 * @return the creds
	 */
	public Object getCreds() {
		return creds;
	}
}
