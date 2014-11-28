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
package org.bibsonomy.webapp.util.spring.security.saml.context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.springframework.security.saml.context.SAMLContextProviderImpl;
import org.springframework.security.saml.context.SAMLMessageContext;

/**
 * This class is necessary to feed a custom relaystate into the spring saml implementation code.
 *  The relaystate is a parameter that is required by by the SAML standard to be send back to the SP when the IdP receives it from the SP.
 *  It allows identification of responses to a particular authentication request.
 *  
 * @author jensi
 */
public class RelayStateSamlContextProviderImpl extends SAMLContextProviderImpl {
	/**
	 * name of the request attribute used to feed a custom relaystate into the spring saml implementation code
	 */
	public static final String SAML_RELAYSTATE_ATTR_NAME = RelayStateSamlContextProviderImpl.class.getName() + ".relaystate";

	@Override
	public SAMLMessageContext getLocalAndPeerEntity(HttpServletRequest request, HttpServletResponse response) throws MetadataProviderException {
		SAMLMessageContext ctx = super.getLocalAndPeerEntity(request, response);
		Object attr = request.getAttribute(SAML_RELAYSTATE_ATTR_NAME);
		if (attr instanceof String) {
			ctx.setRelayState((String)attr);
		}
		return ctx;
	}
}
