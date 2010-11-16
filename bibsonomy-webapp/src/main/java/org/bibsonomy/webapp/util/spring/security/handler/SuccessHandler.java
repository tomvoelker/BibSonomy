package org.bibsonomy.webapp.util.spring.security.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bibsonomy.model.User;
import org.bibsonomy.webapp.util.CookieLogic;
import org.bibsonomy.webapp.util.ResponseLogic;
import org.bibsonomy.webapp.util.spring.security.UserAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import filters.InitUserFilter;

/**
 * @author dzo
 * @version $Id$
 */
public class SuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
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
			
			// TODO: remove when all old jsp sites are ported to the new spring system
			request.setAttribute(InitUserFilter.REQ_ATTRIB_USER, user);
		}
		
		super.onAuthenticationSuccess(request, response, authentication);
	}
}
