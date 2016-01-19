/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
package org.bibsonomy.webapp.filters;


import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.model.User;
import org.bibsonomy.util.spring.security.AuthenticationUtils;

/**
 * This filter redirects Limited Users to the limitedAccountActivation page
 * FIXME: this filter restricts access to some pages => this should be done by spring security
 */
public class LimitedUserFilter implements Filter {
	private final static Log log = LogFactory.getLog(LimitedUserFilter.class);
	
	private List<String> allowedPathPrefixes = Collections.emptyList();

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if (!isLimitedUser() || isAllowedForLimitedUser((HttpServletRequest) request)) {
			chain.doFilter(request, response);
			return;
		}
		log.info("redirecting limited user");
		((HttpServletResponse) response).sendRedirect("/limitedAccountActivation");
	}
	
	protected boolean isAllowedForLimitedUser(HttpServletRequest request) {
		final String requPath = request.getServletPath();
		for (String prefix : this.allowedPathPrefixes) {
			if (requPath.startsWith(prefix)) {
				return true;
			}
		}
		return false;
	}
	
	protected boolean isLimitedUser() {
		User user = AuthenticationUtils.getUserOrNull();
		boolean limitedUser = (user == null) ? false : Role.LIMITED.equals(user.getRole());
		return limitedUser;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void destroy() {
	}

	/**
	 * @return the allowedPathPrefixes
	 */
	public List<String> getAllowedPathPrefixes() {
		return this.allowedPathPrefixes;
	}

	/**
	 * @param allowedPathPrefixes the allowedPathPrefixes to set
	 */
	public void setAllowedPathPrefixes(List<String> allowedPathPrefixes) {
		this.allowedPathPrefixes = allowedPathPrefixes;
	}
}
