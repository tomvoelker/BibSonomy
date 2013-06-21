package org.bibsonomy.webapp.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Wraps the request and overrides getScheme to always return https if enabled.
 * 
 * @author Jens Illig
 * @version $Id$
 */
// TODO: still needed?
public class HttpsPretendingFilter implements Filter {
	
	private boolean enabled;
	
	private String pathPrefix;
	
	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if ((enabled == false) || (pathPrefix != null) && (((HttpServletRequest) request).getServletPath().startsWith(pathPrefix) == false)) {
			chain.doFilter(request, response);
			return;
		}
		
		HttpServletRequestWrapper reqProxy = new HttpServletRequestWrapper((HttpServletRequest)request) {
			@Override
			public String getScheme() {
				return "https";
			}
			
			@Override
			public StringBuffer getRequestURL() {
				StringBuffer url = super.getRequestURL();
				return replaceScheme(url);
			}

			protected StringBuffer replaceScheme(StringBuffer url) {
				int i = url.indexOf(":");
				if ((i > -1) && (i < 6)) {
					url.replace(0, i, "https");
				}
				return url;
			}
			
			@Override
			public String getRequestURI() {
				StringBuffer sb = new StringBuffer(super.getRequestURI());
				return replaceScheme(sb).toString();
			}
		};
		chain.doFilter(reqProxy, response);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}
	
	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return this.enabled;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return the pathPrefix
	 */
	public String getPathPrefix() {
		return this.pathPrefix;
	}

	/**
	 * @param pathPrefix the pathPrefix to set
	 */
	public void setPathPrefix(String pathPrefix) {
		this.pathPrefix = pathPrefix;
	}


}
