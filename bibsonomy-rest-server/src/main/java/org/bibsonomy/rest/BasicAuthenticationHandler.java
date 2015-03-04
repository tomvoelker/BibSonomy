/**
 * BibSonomy-Rest-Server - The REST-server.
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
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.rest;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.LogicInterfaceFactory;
import org.bibsonomy.rest.exceptions.AuthenticationException;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.utils.HeaderUtils;

/**
 * this class authenticates the user using the basic header attribute of the
 * request
 *
 * @author dzo
 */
public class BasicAuthenticationHandler implements AuthenticationHandler<String> {	
	private static final Log log = LogFactory.getLog(BasicAuthenticationHandler.class);
	
	/** to identify HTTP basic authentication. */
	private static final String HTTP_AUTH_BASIC_IDENTIFIER = "Basic ";
	
	
	private LogicInterfaceFactory logicFactory;
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.AuthenticationHandler#extractAuthentication(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public String extractAuthentication(HttpServletRequest request) {
		return request.getHeader(HeaderUtils.HEADER_AUTHORIZATION);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.AuthenticationHandler#canAuthenticateUser(org.apache.http.HttpRequest)
	 */
	@Override
	public boolean canAuthenticateUser(String authenticationHeader) {
		return HeaderUtils.isHttpBasicAuthorization(authenticationHeader);
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.AuthenticationHandler#authenticateUser(org.apache.http.HttpRequest)
	 */
	@Override
	public LogicInterface authenticateUser(String authentication) {
		if (!HeaderUtils.isHttpBasicAuthorization(authentication)) {
			throw new AuthenticationException(NO_AUTH_ERROR);
		}
		try {
			final String basicCookie = new String(Base64.decodeBase64(authentication.substring(HTTP_AUTH_BASIC_IDENTIFIER.length()).getBytes()), RestServlet.RESPONSE_ENCODING);
			final int i = basicCookie.indexOf(':');
			if (i < 0) {
				throw new BadRequestOrResponseException("error decoding authorization header: syntax error");
			}
		
			// check username and password
			final String username = basicCookie.substring(0, i);
			final String apiKey = basicCookie.substring(i + 1);
			log.debug("Username/API-key: " + username + " / " + apiKey);
			try {
				return logicFactory.getLogicAccess(username, apiKey);
			} catch (final AccessDeniedException e) {
				throw new AuthenticationException("Authentication failure: " + e.getMessage());
			}
		} catch (final IOException e) {
			throw new BadRequestOrResponseException("error decoding authorization header: " + e.toString());
		}
	}

	/**
	 * @param logicFactory the logicFactory to set
	 */
	public void setLogicFactory(LogicInterfaceFactory logicFactory) {
		this.logicFactory = logicFactory;
	}

}
