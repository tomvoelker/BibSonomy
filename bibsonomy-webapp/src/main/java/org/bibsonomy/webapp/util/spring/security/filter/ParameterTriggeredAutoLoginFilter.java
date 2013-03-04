package org.bibsonomy.webapp.util.spring.security.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bibsonomy.model.User;
import org.bibsonomy.util.spring.security.AuthenticationUtils;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * Filter triggers a login process if the given request-parameter-name is set to true
 * 
 * @author jensi
 * @version $Id$
 */
public class ParameterTriggeredAutoLoginFilter implements Filter {

	private String loginParameterName;
	private AuthenticationEntryPoint authenticationEntryPoint;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if ("true".equals(request.getParameter(loginParameterName))) {
			User user = AuthenticationUtils.getUserOrNull();
			if (user == null) {
				//((HttpServletRequest) request).getRequestDispatcher("/login_saml").forward(request, response);
				//return;
				authenticationEntryPoint.commence((HttpServletRequest) request, (HttpServletResponse) response, null);
				return;
				//throw new SpecialAuthMethodRequiredException(AuthMethod.SAML);
			}
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
	}

	/**
	 * @return the loginParameterName
	 */
	public String getLoginParameterName() {
		return this.loginParameterName;
	}

	/**
	 * @param loginParameterName the loginParameterName to set
	 */
	public void setLoginParameterName(String loginParameterName) {
		this.loginParameterName = loginParameterName;
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

}
