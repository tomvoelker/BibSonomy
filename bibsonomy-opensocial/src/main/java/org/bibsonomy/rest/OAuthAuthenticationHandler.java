/**
 * BibSonomy-OpenSocial - Implementation of the Opensocial specification and OAuth Security Handling
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
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.rest;

import static org.bibsonomy.util.ValidationUtils.present;

import javax.servlet.http.HttpServletRequest;

import org.apache.shindig.auth.SecurityToken;
import org.bibsonomy.database.ShindigLogicInterfaceFactory;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.opensocial.oauth.OAuthRequestValidator;
import org.bibsonomy.rest.exceptions.AuthenticationException;

/**
 * OAuth authentication
 *
 * @author dzo
 */
public class OAuthAuthenticationHandler implements AuthenticationHandler<SecurityToken> {

	/** handles OAuth requests */
	private OAuthRequestValidator oauthValidator;

	/** logic interface factory for handling oauth requests */
	private ShindigLogicInterfaceFactory oauthLogicFactory;
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.AuthenticationHandler#canAuthenticateUser(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public boolean canAuthenticateUser(SecurityToken securityToken) {
		return present(securityToken);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.AuthenticationHandler#extractAuthentication(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public SecurityToken extractAuthentication(HttpServletRequest request) {
		return this.oauthValidator.getSecurityTokenFromRequest(request);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.AuthenticationHandler#authenticateUser(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public LogicInterface authenticateUser(SecurityToken securityToken) throws AuthenticationException {
		if (securityToken.isAnonymous()) {
			throw new AuthenticationException(AuthenticationHandler.NO_AUTH_ERROR);
		}
		return this.oauthLogicFactory.getLogicAccess(securityToken);
	}

	/**
	 * @param oauthValidator the oauthValidator to set
	 */
	public void setOauthValidator(OAuthRequestValidator oauthValidator) {
		this.oauthValidator = oauthValidator;
	}

	/**
	 * @param oauthLogicFactory the oauthLogicFactory to set
	 */
	public void setOauthLogicFactory(ShindigLogicInterfaceFactory oauthLogicFactory) {
		this.oauthLogicFactory = oauthLogicFactory;
	}
}
