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
package org.bibsonomy.webapp.util.spring.security.userattributemapping;

import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.bibsonomy.model.User;
import org.bibsonomy.model.user.remote.SamlRemoteUserId;
import org.bibsonomy.webapp.util.spring.security.saml.util.SAMLCredentialUtils;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.NameID;
import org.springframework.security.saml.SAMLCredential;

/**
 * Sets user attributes with values from a SAML (Shibboleth) SSO response
 * 
 * @author jensi
 */
public class SamlUserAttributeMapping implements UserAttributeMapping<SAMLCredential, SamlRemoteUserId> {
	private static final Logger log = Logger.getLogger(SamlUserAttributeMapping.class);
	private Map<String, String> samlToUserPropertiesMap;
	private String useridAttributeName;
	
	/**
	 * Sets user attributes with values from a SAML (Shibboleth) SSO response
	 * @param user object to be populated
	 * @param samlCred saml credentials to read from
	 * @return remote user id extrected from src object
	 */
	@Override
	public SamlRemoteUserId populate(User user, SAMLCredential samlCred) {
		SamlRemoteUserId remoteId = getRemoteUserId(samlCred);
		user.setRemoteUserId(remoteId);
		
		writeAttributeDebugLogs(samlCred);
		
		// copy user attributes
		for (Map.Entry<String, String> entry : samlToUserPropertiesMap.entrySet()) {
			final Attribute samlAttr = samlCred.getAttributeByName(entry.getKey());
			Object samlValue = (samlAttr == null) ? null : SAMLCredentialUtils.getSingleStringValueFromAttribute(samlAttr);
			try {
				BeanUtils.setProperty(user, entry.getValue(), samlValue);
			} catch (Exception ex) {
				throw new RuntimeException("exception while setting property '" + entry.getValue() + "'", ex);
			}
		}
		return remoteId;
	}

	private static void writeAttributeDebugLogs(SAMLCredential samlCred) {
		if (log.isDebugEnabled()) {
			StringBuilder sb = new StringBuilder();
			for (Attribute a : samlCred.getAttributes()) {
				sb.append(a.getName()).append(" or simply ").append(a.getFriendlyName()).append(" = ");
				SAMLCredentialUtils.appendAttributesAsArrayString(sb, a.getAttributeValues());
				log.debug(sb.toString());
				sb.setLength(0);
			}
		}
	}
	
	/**
	 * @param samlCred saml credentials to read from
	 * @return remote user id extracted from credentials
	 */
	public SamlRemoteUserId getRemoteUserId(SAMLCredential samlCred) {
		final String userId = getUserId(samlCred);
		Assertion aa = samlCred.getAuthenticationAssertion();
		if (aa == null) {
			return null;
		}
		Issuer issuer = aa.getIssuer();
		if (issuer == null) {
			return null;
		}
		String idP = issuer.getValue();
		return new SamlRemoteUserId(idP, userId);
//		return "dummyUserId";
	}

	/**
	 * @param samlCred response from the remote system
	 * @return local userid of the remote system
	 */
	public String getUserId(SAMLCredential samlCred) {
		writeAttributeDebugLogs(samlCred);
		if (useridAttributeName != null) {
			return SAMLCredentialUtils.getSingleStringValueAttribute(samlCred, useridAttributeName);
		}
		// does this work at all?
		NameID nid = samlCred.getNameID();
		if (nid == null) {
			return null;
		}
		return nid.getValue();
	}

	/**
	 * @return the samlToUserPropertiesMap map with SAML attribute names as keys and {@link User}-property names as values
	 */
	public Map<String, String> getSamlToUserPropertiesMap() {
		return this.samlToUserPropertiesMap;
	}

	/**
	 * @return the useridAttributeName
	 */
	public String getUseridAttributeName() {
		return this.useridAttributeName;
	}

	/**
	 * @param useridAttributeName the useridAttributeName to set
	 */
	public void setUseridAttributeName(String useridAttributeName) {
		this.useridAttributeName = useridAttributeName;
	}

	/**
	 * @param samlToUserPropertiesMap the samlToUserPropertiesMap to set
	 */
	public void setSamlToUserPropertiesMap(Map<String, String> samlToUserPropertiesMap) {
		this.samlToUserPropertiesMap = samlToUserPropertiesMap;
	}

}
