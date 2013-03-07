package org.bibsonomy.webapp.util.spring.security.web;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.spring.security.saml.SamlAuthenticationTool;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * @author jensi
 * @version $Id$
 */
public class RelayStateSettingEntryPoint implements AuthenticationEntryPoint {
	private static final Log log = LogFactory.getLog(RelayStateSettingEntryPoint.class);
	private AuthenticationEntryPoint entryPoint;
	private ApplicationContext applicationContext;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
		SamlAuthenticationTool samlTool = getSamlTool(request);
		if (samlTool != null) {
			samlTool.setRelayState();
		} else {
			log.warn("no samlTool");
		}
		entryPoint.commence(request, response, authException);
	}

	private SamlAuthenticationTool getSamlTool(HttpServletRequest request) {
		RequestLogic reqLogic = new RequestLogic(request);
		Collection<String> allowedParametersForRedirect;
		if ("GET".equalsIgnoreCase(request.getMethod()) == true) {
			allowedParametersForRedirect = null;
		} else {
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
