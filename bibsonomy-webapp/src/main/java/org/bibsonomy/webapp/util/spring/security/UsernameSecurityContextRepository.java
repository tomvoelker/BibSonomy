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
package org.bibsonomy.webapp.util.spring.security;

import static org.bibsonomy.util.ValidationUtils.present;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;

import org.bibsonomy.util.spring.security.RemoteOnlyUserDetails;
import org.bibsonomy.webapp.util.spring.security.authentication.SessionAuthenticationToken;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SaveContextOnUpdateOrErrorResponseWrapper;
import org.springframework.security.web.context.SecurityContextRepository;

/**
 * implements {@link SecurityContextRepository}
 * - saves the <code>username</code> of the logged in user in the session attribute {@value #ATTRIBUTE_LOGIN_USER_NAME}
 * - loads the security context by retrieving the username from the session and loading the userdetails from the provided
 * UserdetailsSerivce {@link #service}
 * 
 * @author dzo
 */
public class UsernameSecurityContextRepository implements SecurityContextRepository {
	private static final String ATTRIBUTE_LOGIN_USER_NAME = "LOGIN_USER_NAME";
	
	
	private static final String ATTRIBUTE_CREDS = "ATTRIBUTE_CREDS";
	
	/**
	 * Delivers details for each given user.
	 */
	private UserDetailsService service;
	private final AuthenticationTrustResolver authenticationTrustResolver = new AuthenticationTrustResolverImpl();

	/**
	 * Checks for a user name in the session. If one is found, the corresponding
	 * user details are extracted and the user is stored as request attribute.
	 * 
	 * @see org.springframework.security.web.context.SecurityContextRepository#loadContext(org.springframework.security.web.context.HttpRequestResponseHolder)
	 */
	@Override
	public SecurityContext loadContext(final HttpRequestResponseHolder requestResponseHolder) {
		final HttpServletRequest request = requestResponseHolder.getRequest();
		final SecurityContextImpl securityContext = new SecurityContextImpl();
		SessionAuthenticationToken authentication = null;
		
		final String username = getLoginUser(request);
		if (present(username)) {
			/*
			 * user name found in session -> get the corresponding user
			 */
			final UserDetails user = this.service.loadUserByUsername(username);
			authentication = new SessionAuthenticationToken(user, user.getAuthorities());
		}
		final Object creds = getSessionAttribute(request, ATTRIBUTE_CREDS);
		if (creds != null) {
			if (authentication == null) {
				authentication = new SessionAuthenticationToken(null, null);
			}
			authentication.setCreds(creds);
			removeSessionAttribute(request, ATTRIBUTE_CREDS);
		}
		if (authentication != null) {
			securityContext.setAuthentication(authentication);
		}
		
		final UsernameSecurityWrapper wrapper = new UsernameSecurityWrapper(requestResponseHolder.getResponse(), request, false);
		requestResponseHolder.setResponse(wrapper);
		return securityContext;
	}
	
	
	/**
	 * Stores the user name in the session.
	 * 
	 * @see org.springframework.security.web.context.SecurityContextRepository#saveContext(org.springframework.security.core.context.SecurityContext, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void saveContext(final SecurityContext context, final HttpServletRequest request, HttpServletResponse response) {
		while ( ((response instanceof UsernameSecurityWrapper) == false) && (response instanceof HttpServletResponseWrapper) ) {
			response = (HttpServletResponse) ((HttpServletResponseWrapper)response).getResponse();
		}
		final UsernameSecurityWrapper wrapper = (UsernameSecurityWrapper) response;
		if (!wrapper.isContextSaved()) {
			wrapper.saveContext(context);
		}
	}

	/**
	 * The name of the logged in user is stored in the session. This method 
	 * extracts the name from the session.
	 * 
	 * @param request
	 * @return
	 */
	private static String getLoginUser(final HttpServletRequest request) {
		return (String) getSessionAttribute(request, ATTRIBUTE_LOGIN_USER_NAME);
	}


	private static Object getSessionAttribute(final HttpServletRequest request, String attr) {
		final HttpSession session = request.getSession(false);
		if (session == null) {
			return null;
		}
		
		return session.getAttribute(attr);
	}
	
	private static void removeSessionAttribute(final HttpServletRequest request, String attr) {
		final HttpSession session = request.getSession(false);
		if (session == null) {
			return;
		}
		session.removeAttribute(attr);
	}
	
	@Override
	public boolean containsContext(HttpServletRequest request) {
		return getLoginUser(request) != null;
	}

	/**
	 * @param service the service to set
	 */
	public void setService(UserDetailsService service) {
		this.service = service;
	}
	
	private final class UsernameSecurityWrapper extends SaveContextOnUpdateOrErrorResponseWrapper {
		private HttpServletRequest request;
		
		/**
		 * @param response
		 * @param request 
		 * @param disableUrlRewriting
		 */
		public UsernameSecurityWrapper(HttpServletResponse response, HttpServletRequest request, boolean disableUrlRewriting) {
			super(response, disableUrlRewriting);
			this.request = request;
		}

		/* (non-Javadoc)
		 * @see org.springframework.security.web.context.SaveContextOnUpdateOrErrorResponseWrapper#saveContext(org.springframework.security.core.context.SecurityContext)
		 */
		@Override
		public void saveContext(SecurityContext context) {
			final Authentication authentication = context.getAuthentication();
			if (authenticationTrustResolver.isAnonymous(authentication)) {
				return;
			}
			
			/*
			 * If an authentication is present, we store the user name in the 
			 * session. Note that we /always/ store it - also when it already 
			 * contained in the session (i.e., we don't check for 
			 * !this.containsContext(request)). Thus, the session should time out
			 * after XX minutes of /inactivity/.
			 */
			if (present(authentication)) {
				Object principal = authentication.getPrincipal();
				if (principal instanceof UserDetails) {
					final UserDetails user = (UserDetails) principal;
					final String loginUsername = user.getUsername();
					final HttpSession session = request.getSession(true);
					session.setAttribute(ATTRIBUTE_LOGIN_USER_NAME, loginUsername);
					if (principal instanceof RemoteOnlyUserDetails) {
						// this happens in cases when SAML credentials are sent that could not be mapped to a local user
						//  -> remember credentials here so they can be handled after a redirect to the entry controller
						Object creds = authentication.getCredentials();
						if (creds instanceof SAMLCredential) {
							session.setAttribute(ATTRIBUTE_CREDS, creds);
						}
					}
				} else {
					final HttpSession session = request.getSession(false);
					if (session != null) {
						session.removeAttribute(ATTRIBUTE_LOGIN_USER_NAME);
						session.removeAttribute(ATTRIBUTE_CREDS);
					}
				}
			}
		}
	}

}
