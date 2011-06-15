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
 * @version $Id$
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
		if (present(this.loginFormUrl) && targetUrl.indexOf(this.loginFormUrl) == 1) {
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
