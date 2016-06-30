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
package org.bibsonomy.webapp.util.spring.security.rememberMeServices;

import javax.servlet.http.HttpServletRequest;

import org.bibsonomy.util.UrlParameterExtractor;
import org.bibsonomy.util.ValidationUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.saml.SAMLCredential;

/**
 * Adapter to make the {@link #getCookieName()} of
 * {@link org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices}
 * public
 * 
 * Also checks if rememberMe services are requested inside the SAML relaystate
 * 
 * @author dzo
 */
public class TokenBasedRememberMeServices extends org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices implements CookieBasedRememberMeServices {
	private UrlParameterExtractor paramExtractor;
	
	/**
	 * default constructor
	 * 
	 * @param key
	 * @param userDetailsService
	 */
	public TokenBasedRememberMeServices(final String key, final UserDetailsService userDetailsService) {
		super(key, userDetailsService);
	}

	@Override
	public String getCookieName() {
		return super.getCookieName();
	}

	@Override
	protected boolean rememberMeRequested(HttpServletRequest request, String parameter) {
		return (super.rememberMeRequested(request, parameter) || rememberMeRequestedInSamlRelayState());
	}

	/**
	 * SAML (Shibboleth) Single-Sign-On authenticates the user in a redirect coming back from the IdP. This redirect URL does not contain the rememberMe parameter anymore. Instead any state must be preserved inside a relayState parameter which is passed through the redirect process
	 * @return whether rememberme was requested for a SAML login
	 */
	private boolean rememberMeRequestedInSamlRelayState() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			Object credentials = authentication.getCredentials();
			if (credentials instanceof SAMLCredential) {
				SAMLCredential samlCredential = (SAMLCredential) credentials;
				String relayState = samlCredential.getRelayState();
				if (ValidationUtils.present(relayState)) {
					final String parameterValue = getParamExtractor().parseParameterValueFromUrl(relayState);
					return isPositiveRememberMeValue(parameterValue);
				}
			}
		}
		return false;
	}
	
	@Override
	public void setParameter(String parameter) {
		super.setParameter(parameter);
		this.paramExtractor = null;
	}
	
	private UrlParameterExtractor getParamExtractor() {
		if (this.paramExtractor == null) {
			this.paramExtractor =new UrlParameterExtractor(getParameter());
		}
		return this.paramExtractor;
	}

	private static boolean isPositiveRememberMeValue(String paramValue) {
		return ("true".equalsIgnoreCase(paramValue) || "on".equalsIgnoreCase(paramValue) || "yes".equalsIgnoreCase(paramValue) || "1".equals(paramValue));
	}

}
