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
package org.bibsonomy.webapp.util.spring.security.exceptions;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;

/**
 * Signals that a user that was successfully authenticated using SAML was not
 * found in the database. 
 * 
 * @author jensi
 */
public class SamlUsernameNotFoundException extends UsernameNotFoundException {
	private static final long serialVersionUID = 317691533775866307L;
	
	private final SAMLCredential samlCreds;
	
	/**
	 * @param msg
	 * @param samlCreds credentials as received in the assertion from the idp
	 */
	public SamlUsernameNotFoundException(String msg, SAMLCredential samlCreds) {
		super(msg);
		this.samlCreds = samlCreds;
	}
	
	/**
	 * {@link #SamlUsernameNotFoundException(String, SAMLCredential)} but with standard message
	 * @param samlCreds credentials as received in the assertion from the idp
	 */
	public SamlUsernameNotFoundException(SAMLCredential samlCreds) {
		this("SAML id not found in database", samlCreds);
	}

	/**
	 * @return the credentials from the SAML assertion message
	 */
	public SAMLCredential getSamlCreds() {
		return this.samlCreds;
	}

}
