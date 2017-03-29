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

import org.bibsonomy.webapp.util.spring.security.saml.credential.checker.SAMLCredentialChecker;
import org.springframework.security.saml.SAMLAuthenticationProvider;
import org.springframework.security.saml.SAMLCredential;

/**
 * {@link SAMLAuthenticationProvider} that prechecks saml credentials before
 * loading the userdetails
 *
 * @author dzo
 */
public class SAMLRestrictedAuthenticationProvider extends SAMLAuthenticationProvider {
	
	private SAMLCredentialChecker preSAMLChecker;
	
	/* (non-Javadoc)
	 * @see org.springframework.security.saml.SAMLAuthenticationProvider#getUserDetails(org.springframework.security.saml.SAMLCredential)
	 */
	@Override
	protected Object getUserDetails(SAMLCredential credential) {
		this.preSAMLChecker.checkCredential(credential);
		return super.getUserDetails(credential);
	}

	/**
	 * @param preSAMLChecker the preSAMLChecker to set
	 */
	public void setPreSAMLChecker(SAMLCredentialChecker preSAMLChecker) {
		this.preSAMLChecker = preSAMLChecker;
	}
}
