package org.bibsonomy.webapp.util.spring.security;

import java.util.Collection;

import org.bibsonomy.model.logic.LogicInterface;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

/**
 * @author dzo
 * @version $Id$
 */
public class LDAPUserDetailsServiceMapper implements UserDetailsContextMapper {
	
	private UserDetailsService userDetailsService;
	private LogicInterface adminLogic;
	
	@Override
	public UserDetails mapUserFromContext(DirContextOperations ctx, String username, Collection<GrantedAuthority> authority) {
		final String systemName = this.adminLogic.getUsernameByLdapUserId(username);
		if (systemName == null) {
			throw new UsernameNotFoundException("", ctx); // TODO
		}
		
		// FIXME: check Role.Deleted
		return this.userDetailsService.loadUserByUsername(systemName);
	}

	/**
	 * @return the adminLogic
	 */
	public LogicInterface getAdminLogic() {
		return this.adminLogic;
	}

	/**
	 * @param adminLogic the adminLogic to set
	 */
	public void setAdminLogic(LogicInterface adminLogic) {
		this.adminLogic = adminLogic;
	}

	@Override
	public void mapUserToContext(UserDetails user, DirContextAdapter ctx) {
		// TODO what to do?!
	}

	/**
	 * @param userDetailsService the userDetailsService to set
	 */
	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	/**
	 * @return the userDetailsService
	 */
	public UserDetailsService getUserDetailsService() {
		return userDetailsService;
	}

}
