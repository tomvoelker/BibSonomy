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
package org.bibsonomy.webapp.util.spring.security.authentication;

import java.util.Collections;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.saml.SAMLCredential;

/**
 * @author jensi
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
