/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
package org.bibsonomy.webapp.util.spring.security.userdetails;

import org.bibsonomy.model.User;
import org.bibsonomy.util.spring.security.UserAdapter;
import org.bibsonomy.webapp.util.spring.security.exceptions.OpenIdUsernameNotFoundException;
import org.bibsonomy.webapp.util.spring.security.userdetailsservice.DatabaseUserDetailsService;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.openid.OpenIDAuthenticationToken;

/**
 * @author dzo
 */
public class OpenIdAuthenticationUserDetailsService extends DatabaseUserDetailsService implements AuthenticationUserDetailsService<OpenIDAuthenticationToken> {

	@Override
	public UserDetails loadUserDetails(final OpenIDAuthenticationToken token) throws UsernameNotFoundException {
		final String openID = token.getIdentityUrl();
		
		final String username = this.adminLogic.getOpenIDUser(openID == null ? null : openID.trim());
		
		if (username == null) {
			throw new OpenIdUsernameNotFoundException("OpenID not found in database"); 
		}
		
		final User user = this.getUserFromDatabase(username);
		final UserAdapter userAdapter = new UserAdapter(user);
		
		if (!userAdapter.isEnabled()) {
			throw new DisabledException("user was deleted");
		}
		
		return userAdapter;
	}

}
