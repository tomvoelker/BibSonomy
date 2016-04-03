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

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Filter sets everything in the response what could make clients
 * not cache it.
 * 
 * @author Jens Illig
 * @author rja
 */
public class CacheFilter implements Filter {
	
	private static final int CACHE_TIME = 15 * 60; // 15 minutes
	
	private static final String PRAGMA_HEADER_KEY = "Pragma";
	private static final String CACHE_CONTROL_HEADER_KEY = "Cache-Control";
	
	private Pattern cachePattern = Pattern.compile("^/(resources)/.*");
	
	@Override
	public void destroy() {
		// noop
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		final HttpServletRequest httpRequest = (HttpServletRequest)request;
		final String requestURI = httpRequest.getRequestURI();
		final String protocol = request.getProtocol();
		final boolean isHttp11 = "HTTP/1.1".equals(protocol);
		final HttpServletResponse httpResponse = (HttpServletResponse) response;
		
		/*
		 * handle resource files (CSS, JPEG/PNG, JavaScript) ... 
		 */
		if (present(requestURI) && this.cachePattern.matcher(requestURI).matches()) {
			/*
			 * cache resources for CACHE_TIME
			 */
			if (!isHttp11) {
				httpResponse.setHeader(PRAGMA_HEADER_KEY, "cache");
			} else {
				httpResponse.setHeader(CACHE_CONTROL_HEADER_KEY, "max-age=" + CACHE_TIME);
			}
			filterChain.doFilter(request, response);
			return;
		}
		
		/*
		 * FIXME: workaround for IE6 bug 
		 * http://www.somacon.com/p106.php
		 * http://www.brookes.ac.uk/mediaworkshop/brookesvirtual/faqs.html#cache
		 */
		if (request.isSecure()) {
			if (httpRequest.getRequestURI().startsWith("/documents/")) {
				/*
				 * don't modify cache header for PDF documents when SSL is enabled
				 */
				filterChain.doFilter(request, response);
				return;
			}
		}
		
		if (!isHttp11) {
			httpResponse.setHeader(PRAGMA_HEADER_KEY, "no-cache");
		} else {
			httpResponse.setHeader(CACHE_CONTROL_HEADER_KEY, "no-cache");
		}
		httpResponse.setDateHeader("Expires",-1);
		httpResponse.setDateHeader("Last-Modified",0);
		filterChain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		final String initParameterExcludePatterns = filterConfig.getInitParameter("cachePattern");
		if (present(initParameterExcludePatterns)) {
			this.cachePattern = Pattern.compile(initParameterExcludePatterns);
		}
	}
}
