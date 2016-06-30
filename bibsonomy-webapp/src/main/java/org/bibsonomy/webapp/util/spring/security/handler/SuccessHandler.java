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
package org.bibsonomy.webapp.util.spring.security.handler;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bibsonomy.model.User;
import org.bibsonomy.util.spring.security.UserAdapter;
import org.bibsonomy.webapp.util.CookieLogic;
import org.bibsonomy.webapp.util.ResponseLogic;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

/**
 * after the user was successfully authenticated we need to set the spammer cookie
 * this {@link AuthenticationSuccessHandler} sets the spammer cookie before redirecting
 * the user
 * 
 * @author dzo
 */
public class SuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
	
	private String loginFormUrl = "";
	
	@Override
	public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) throws IOException, ServletException {
		final Object principal = authentication.getPrincipal();
		if (principal instanceof UserAdapter) {
			final UserAdapter adapter = (UserAdapter) principal;
			final User user = adapter.getUser();
			
			/* 
			 * add spammer cookie
			 */
			final CookieLogic logic = new CookieLogic();
			logic.setResponseLogic(new ResponseLogic(response));
			logic.addSpammerCookie(user.isSpammer());
			// we need to create a session before the parent method redirects to
			// the authentication success url. This prevents
			// "IllegalStateException: Cannot create a session after the response has been committed"
			// which would otherwise be trigered by the
			// UsernameSecurityContextRepository called from Spring's
			// SecurityContextPersistenceFilter after the redirect has already
			// been sent on the same request.
			request.getSession();
		}
		
		super.onAuthenticationSuccess(request, response, authentication);
	}
	
	/**
	 * returns an empty string if targeturl is the login page otherwise
	 * the user will be redirected to the login site and gets an access denied
	 * view
	 * @see org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler#determineTargetUrl(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected String determineTargetUrl(final HttpServletRequest request, final HttpServletResponse response) {
		final String targetUrl = super.determineTargetUrl(request, response);
		
		// == 1 because loginUrl contains leading /
		if (present(this.loginFormUrl) && this.loginFormUrl.indexOf(targetUrl) == 1) {
			return "";
		}
		
		return targetUrl;
	}

	/**
	 * @param loginFormUrl the loginFormUrl to set
	 */
	public void setLoginFormUrl(final String loginFormUrl) {
		this.loginFormUrl = loginFormUrl;
	}
}
