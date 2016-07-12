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
package org.bibsonomy.webapp.util.spring.security.provider;

import java.util.Collection;
import java.util.Date;

import org.bibsonomy.util.spring.security.RemoteOnlyUserDetails;
import org.bibsonomy.webapp.util.spring.security.authentication.SamlCredAuthToken;
import org.bibsonomy.webapp.util.spring.security.exceptions.SamlUsernameNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.providers.ExpiringUsernameAuthenticationToken;
import org.springframework.security.saml.SAMLAuthenticationProvider;
import org.springframework.security.saml.SAMLCredential;

/**
 * Simple {@link AuthenticationProvider} that loads userDetails from a SAML.
 * This is used for a second 'authentication' after a redirect from the assertion
 * consumer service (which got the SAML authentication response from the IdP) back
 * to the original URL.
 * 
 * @author jensi
 */
public class SamlCredAuthProvider extends SAMLAuthenticationProvider {

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		if (!supports(authentication.getClass())) {
			throw new IllegalArgumentException("unsupported Authentication type " + authentication.getClass());
		}
		SAMLCredential credential = ((SamlCredAuthToken) authentication).getCredentials();
		Object userDetails = getUserDetails(credential);
		if (userDetails instanceof RemoteOnlyUserDetails) {
			throw new SamlUsernameNotFoundException(credential);
		}
		Object principal = getPrincipal(credential, userDetails);
		Collection<? extends GrantedAuthority> entitlements = getEntitlements(credential, userDetails);

		Date expiration = getExpirationDate(credential);
		ExpiringUsernameAuthenticationToken result = new ExpiringUsernameAuthenticationToken(expiration, principal, credential, entitlements);
		result.setDetails(userDetails);
		
		return result;
	}

	@SuppressWarnings("rawtypes")
	// rawtype argument is necessary because it is used in superclass
	@Override
	public boolean supports(Class authentication) {
		return (SamlCredAuthToken.class.isAssignableFrom(authentication));
	}

}
