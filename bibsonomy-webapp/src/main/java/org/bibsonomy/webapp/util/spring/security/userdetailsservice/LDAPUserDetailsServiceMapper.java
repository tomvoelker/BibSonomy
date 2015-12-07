/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.util.spring.security.userdetailsservice;

import java.util.Collection;

import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.util.spring.security.exceptions.LdapUsernameNotFoundException;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

/**
 * maps ldap users to BibSonomy system users
 * 
 * @author dzo
 */
public class LDAPUserDetailsServiceMapper implements UserDetailsContextMapper {
	
	private UserDetailsService userDetailsService;
	private LogicInterface adminLogic;
	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.security.ldap.userdetails.UserDetailsContextMapper#mapUserFromContext(org.springframework.ldap.core.DirContextOperations, java.lang.String, java.util.Collection)
	 */
	@Override
	public UserDetails mapUserFromContext(final DirContextOperations ctx, final String username, final Collection<? extends GrantedAuthority> authorities) {
		final String systemName = this.adminLogic.getUsernameByLdapUserId(username);
		if (systemName == null) {
			throw new LdapUsernameNotFoundException("LDAP id not found in database", ctx);
		}
		
		final UserDetails loadedUser = this.userDetailsService.loadUserByUsername(systemName);
		
		// TODO: are we missing something else?
		if (!loadedUser.isEnabled()) {
			throw new DisabledException("user was deleted");
		}
		
		return loadedUser;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.security.ldap.userdetails.UserDetailsContextMapper#mapUserToContext(org.springframework.security.core.userdetails.UserDetails, org.springframework.ldap.core.DirContextAdapter)
	 */
	@Override
	public void mapUserToContext(final UserDetails user, final DirContextAdapter ctx) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @param adminLogic the adminLogic to set
	 */
	public void setAdminLogic(final LogicInterface adminLogic) {
		this.adminLogic = adminLogic;
	}

	/**
	 * @param userDetailsService the userDetailsService to set
	 */
	public void setUserDetailsService(final UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}
}
