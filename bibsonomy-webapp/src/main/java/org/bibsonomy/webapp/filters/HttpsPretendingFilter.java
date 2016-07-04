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
