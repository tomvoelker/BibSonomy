/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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

import org.bibsonomy.webapp.util.spring.security.exceptions.OpenIdUsernameNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * When a user is successfully authenticated using OpenID but we can't find him
 * in our database, we want to send him to a registration form where the fields
 * are filled with his OpenID data.
 * 
 * The only place to add this information is here, because here we find out
 * that the user is not registered, yet, we don't have the OpenID authentication
 * to put it into the exception.
 * 
 * @author rja
 */
public class OpenIDAuthenticationProvider extends org.springframework.security.openid.OpenIDAuthenticationProvider {

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		try {
			return super.authenticate(authentication);
		} catch (final OpenIdUsernameNotFoundException e) {
			/*
			 * Add user data to exception and re-throw it. The data is later 
			 * stored in the session and used for filling the registration form. 
			 */
			e.setAuthentication(authentication);
			throw e;
		}
	}
}
