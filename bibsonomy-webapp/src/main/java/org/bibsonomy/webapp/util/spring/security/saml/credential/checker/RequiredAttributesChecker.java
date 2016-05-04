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
package org.bibsonomy.webapp.util.spring.security.saml.credential.checker;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bibsonomy.webapp.util.spring.security.exceptions.UseNotAllowedException;
import org.bibsonomy.webapp.util.spring.security.saml.util.SAMLCredentialUtils;
import org.opensaml.saml2.core.Attribute;
import org.springframework.security.saml.SAMLCredential;

/**
 * checks for required attributes
 *
 * @author dzo
 */
public class RequiredAttributesChecker implements SAMLCredentialChecker {
	
	private Map<String, String> requiredAttributes = Collections.emptyMap();
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.util.spring.security.saml.credential.checker.SAMLCredentialChecker#checkCredential(org.springframework.security.saml.SAMLCredential)
	 */
	@Override
	public void checkCredential(final SAMLCredential credential) {
		for (final Entry<String, String> requiredAttributeEntry : this.requiredAttributes.entrySet()) {
			final Attribute requiredAttribute = credential.getAttributeByName(requiredAttributeEntry.getKey());
			if (requiredAttribute == null) {
				throw new UseNotAllowedException("user can't use this app");
			}
			final List<String> values = SAMLCredentialUtils.getStringListFromAttribute(requiredAttribute);
			if (!values.contains(requiredAttributeEntry.getValue())) {
				throw new UseNotAllowedException("user can't use this app");
			}
		}
	}

	/**
	 * @param requiredAttributes the requiredAttributes to set
	 */
	public void setRequiredAttributes(Map<String, String> requiredAttributes) {
		this.requiredAttributes = requiredAttributes;
	}

}
