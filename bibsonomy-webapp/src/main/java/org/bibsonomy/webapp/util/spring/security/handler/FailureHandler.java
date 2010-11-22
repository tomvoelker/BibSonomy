package org.bibsonomy.webapp.util.spring.security.handler;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.TeerGrube;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

/**
 * @author dzo
 * @version $Id$
 */
public class FailureHandler extends SimpleUrlAuthenticationFailureHandler {
	
	private TeerGrube grube;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
		// TODO: remove instanceof chain
		if (exception instanceof BadCredentialsException) {
			/*
			 * log failure
			 * Brute Force Attacks!! 
			 */
			final BadCredentialsException badCredentialsException = (BadCredentialsException) exception;
			final Authentication authentication = badCredentialsException.getAuthentication();
			final String username = (String) authentication.getPrincipal();
			if (present(username)) {
				final RequestLogic requestLogic = new RequestLogic(request);
				
				this.grube.add(username);
				this.grube.add(requestLogic.getInetAddress());
			}
		}
		
		super.onAuthenticationFailure(request, response, exception);
	}

	/**
	 * @param grube the grube to set
	 */
	public void setGrube(TeerGrube grube) {
		this.grube = grube;
	}
}
