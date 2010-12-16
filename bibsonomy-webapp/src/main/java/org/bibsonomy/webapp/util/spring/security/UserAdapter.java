package org.bibsonomy.webapp.util.spring.security;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

import org.bibsonomy.common.enums.Role;
import org.bibsonomy.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Wrapper around our user object to make it available as UserDetails.
 * 
 * @author dzo
 * @version $Id$
 */
public class UserAdapter implements UserDetails {
	private static final long serialVersionUID = -3926600488722547211L;
	
	private final User user;

	/**
	 * @param user the user to adapt
	 */
	public UserAdapter(final User user) {
		this.user = user;
	}
	
	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	@Override
	public Collection<GrantedAuthority> getAuthorities() {
		final Collection<GrantedAuthority> authorities = new LinkedHashSet<GrantedAuthority>();
		authorities.add(new GrantedAuthorityImpl("ROLE_USER"));
		if (Role.ADMIN.equals(this.user.getRole())) {
			authorities.add(new GrantedAuthorityImpl("ROLE_ADMIN"));
		}
		
		return Collections.unmodifiableCollection(authorities);
	}

	@Override
	public String getPassword() {
		return this.user.getPassword();
	}

	@Override
	public String getUsername() {
		return this.user.getName();
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
		return !Role.DELETED.equals(this.user.getRole()) ;
	}
	
	@Override
	public String toString() {
		return user == null ? "" : user.toString(); 
	}
}
