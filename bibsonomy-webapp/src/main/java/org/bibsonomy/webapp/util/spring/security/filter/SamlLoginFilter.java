package org.bibsonomy.webapp.util.spring.security.filter;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bibsonomy.webapp.controller.actions.VuFindUserInitController;
import org.bibsonomy.webapp.util.spring.security.authentication.SamlCredAuthToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;

/**
 * @author jensi
 * @version $Id$
 */
public class SamlLoginFilter extends AbstractAuthenticationProcessingFilter {
	
	private AuthenticationEntryPoint authenticationEntryPoint;
	private RequestCache requestCache = new HttpSessionRequestCache();
	
	protected SamlLoginFilter() {
		super("/login_saml");
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
		SAMLCredential samlCreds = VuFindUserInitController.getSamlCreds();
		if (samlCreds == null) {
			sendStartAuthentication(request, response);
			return null;
		}
		return getAuthenticationManager().authenticate(new SamlCredAuthToken(samlCreds));

		/*
		Authentication auth = VuFindUserInitController.getAuth();
		if (auth == null) {
			throw new AuthenticationCredentialsNotFoundException("no auth");
		}
		Object principal = auth.getPrincipal();
		if ((principal instanceof UserDetails) == false) {
			throw new AuthenticationCredentialsNotFoundException("no userdetails");
		}
		if (principal instanceof RemoteOnlyUserDetails) {
			samlCreds = VuFindUserInitController.getSamlCreds();
			if (samlCreds == null) {
				throw new AuthenticationCredentialsNotFoundException("no saml credentials");
			}
			throw new SamlUsernameNotFoundException("SAML userid not found in database", samlCreds);
			//throw new SamlUsernameNotFoundException(((RemoteOnlyUserDetails) principal).getUsername(), samlCreds);
		}
		return auth;*/
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
