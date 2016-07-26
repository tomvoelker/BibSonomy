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
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.util.spring.security.AuthenticationUtils;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * Filter triggers a login process if the given request-parameter-name is set to true
 * 
 * @author jensi
 */
public class ParameterTriggeredAutoLoginFilter implements Filter {

	private String loginParameterName;
	private AuthenticationEntryPoint authenticationEntryPoint;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if (ValidationUtils.present(request.getParameter(loginParameterName))) {
			User user = AuthenticationUtils.getUserOrNull();
			if (user == null) {
				authenticationEntryPoint.commence((HttpServletRequest) request, (HttpServletResponse) response, null);
				return;
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
