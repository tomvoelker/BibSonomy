package org.bibsonomy.webapp.util.spring.security.entrypoints;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bibsonomy.common.enums.AuthMethod;
import org.bibsonomy.webapp.util.spring.security.exceptions.UnsupportedAuthenticationMethodException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * @author jensi
 * @version $Id$
 */
public class UnsupportedAuthenticationMethodDummyEntryPoint implements AuthenticationEntryPoint, Filter  {
	private AuthMethod authMethod;
	
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
		throw new UnsupportedAuthenticationMethodException(authMethod);
	}

	/**
	 * @return the authMethod
	 */
	public AuthMethod getAuthMethod() {
		return this.authMethod;
	}

	/**
	 * @param authMethod the authMethod to set
	 */
	public void setAuthMethod(AuthMethod authMethod) {
		this.authMethod = authMethod;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		this.commence(null, null, null);
	}

	@Override
	public void destroy() {
	}

	
}
