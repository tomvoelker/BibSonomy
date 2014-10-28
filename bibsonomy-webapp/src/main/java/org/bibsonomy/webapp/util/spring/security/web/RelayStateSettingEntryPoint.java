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
