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
package org.bibsonomy.webapp.util.spring.security.filter;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bibsonomy.webapp.controller.actions.VuFindUserInitController;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.spring.security.exceptions.SamlUsernameNotFoundException;
import org.bibsonomy.webapp.util.spring.security.saml.SamlAuthenticationTool;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;

/**
 * @author jensi
 */
public class SamlLoginFilter extends AbstractAuthenticationProcessingFilter {
	
	private AuthenticationEntryPoint authenticationEntryPoint;
	private RequestCache requestCache = new HttpSessionRequestCache();
	
	protected SamlLoginFilter() {
		super("/login_saml");
		setContinueChainBeforeSuccessfulAuthentication(true);
		setAuthenticationSuccessHandler(new AuthenticationSuccessHandler() {
			
			@Override
			public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
				
			}
		});
	}
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
		if (getSamlAuthTool(request).isFreshlyAuthenticated() == false) {
			sendStartAuthentication(request, response);
			return null;
		}

		Authentication auth = VuFindUserInitController.getAuth();
		if (auth == null) {
			// actually this is not really bad credentials. It just means something went wrong for whatever reason.
			throw new BadCredentialsException("login failed");
		}
		
		if (auth.getPrincipal() == null) {
			if (auth.getCredentials() instanceof SAMLCredential) {
				throw new SamlUsernameNotFoundException("empty session authentication without pricipal but with saml-credentials", (SAMLCredential) auth.getCredentials());
			}
			throw new BadCredentialsException("no user");
		}
		return auth;
	}

	protected SamlAuthenticationTool getSamlAuthTool(HttpServletRequest request) {
		final SamlAuthenticationTool samlAuthTool = new SamlAuthenticationTool(new RequestLogic(request), null);
		return samlAuthTool;
	}

	protected void sendStartAuthentication(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		// SEC-112: Clear the SecurityContextHolder's Authentication, as the
		// existing Authentication is no longer considered valid
		SecurityContextHolder.getContext().setAuthentication(null);
		requestCache.saveRequest(request, response);
		logger.debug("Calling Authentication entry point.");
		authenticationEntryPoint.commence(request, response, null);
	}

	/**
	 * @return the authenticationEntryPoint
	 */
	public AuthenticationEntryPoint getAuthenticationEntryPoint() {
		return this.authenticationEntryPoint;
	}

	/**
	 * @param authenticationEntryPoint the authenticationEntryPoint to set
	 */
	public void setAuthenticationEntryPoint(AuthenticationEntryPoint authenticationEntryPoint) {
		this.authenticationEntryPoint = authenticationEntryPoint;
	}

	/**
	 * @return the requestCache
	 */
	public RequestCache getRequestCache() {
		return this.requestCache;
	}

	/**
	 * @param requestCache the requestCache to set
	 */
	public void setRequestCache(RequestCache requestCache) {
		this.requestCache = requestCache;
	}

}
