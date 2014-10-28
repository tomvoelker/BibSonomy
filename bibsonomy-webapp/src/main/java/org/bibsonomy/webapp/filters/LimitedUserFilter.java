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
