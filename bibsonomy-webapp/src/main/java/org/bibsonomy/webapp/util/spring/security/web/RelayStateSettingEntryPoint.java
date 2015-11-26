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
package org.bibsonomy.webapp.util.spring.security.web;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.spring.security.saml.SamlAuthenticationTool;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * @author jensi
 */
public class RelayStateSettingEntryPoint implements AuthenticationEntryPoint {
	private AuthenticationEntryPoint entryPoint;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
		getSamlTool(request).setRelayState();
		entryPoint.commence(request, response, authException);
	}

	private SamlAuthenticationTool getSamlTool(HttpServletRequest request) {
		RequestLogic reqLogic = new RequestLogic(request);
		Collection<String> allowedParametersForRedirect;
		if ("GET".equalsIgnoreCase(request.getMethod())) {
			// keep all get parameters
			allowedParametersForRedirect = null;
		} else {
			// no parameters from a post request are allowed to be copied via the relaystate to a later redirect
			allowedParametersForRedirect = Collections.emptySet();
		}
		return new SamlAuthenticationTool(reqLogic, allowedParametersForRedirect);
	}

	/**
	 * @return the entryPoint
	 */
	public AuthenticationEntryPoint getEntryPoint() {
		return this.entryPoint;
	}

	/**
	 * @param entryPoint the entryPoint to set
	 */
	public void setEntryPoint(AuthenticationEntryPoint entryPoint) {
		this.entryPoint = entryPoint;
	}

}
