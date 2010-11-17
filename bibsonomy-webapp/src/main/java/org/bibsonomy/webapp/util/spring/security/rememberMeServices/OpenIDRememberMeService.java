package org.bibsonomy.webapp.util.spring.security.rememberMeServices;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bibsonomy.model.User;
import org.bibsonomy.webapp.util.CookieLogic;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.ResponseLogic;
import org.bibsonomy.webapp.util.spring.security.UserAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.openid.OpenIDAuthenticationToken;
import org.springframework.security.web.authentication.RememberMeServices;

/**
 * @author dzo
 * @version $Id$
 */
public class OpenIDRememberMeService implements RememberMeServices {

	@Override
	public Authentication autoLogin(final HttpServletRequest request, final HttpServletResponse response) {
		// noop
		return null;
	}

	@Override
	public void loginFail(final HttpServletRequest request, final HttpServletResponse response) {
		// noop
	}

	@Override
	public void loginSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication successfulAuthentication) {
		final OpenIDAuthenticationToken token = (OpenIDAuthenticationToken) successfulAuthentication;
		final Object principal = token.getPrincipal();
		
		if (principal instanceof UserAdapter) {
			final UserAdapter userAdapter = (UserAdapter) principal;
			final User loginUser = userAdapter.getUser();
			final String username = loginUser.getName();
			final String openID = token.getIdentityUrl();
			final String passwordHash = loginUser.getPassword(); // TODO: @see UserOpenIDRegistrationController
			
			final RequestLogic requestLogic = new RequestLogic(request);
			final ResponseLogic responseLogic = new ResponseLogic(response);
			final CookieLogic cookieLogic = new CookieLogic();
			cookieLogic.setRequestLogic(requestLogic);
			cookieLogic.setResponseLogic(responseLogic);
			
			// cookieLogic.addOpenIDCookie(username, openID, passwordHash);
		}
	}

}
