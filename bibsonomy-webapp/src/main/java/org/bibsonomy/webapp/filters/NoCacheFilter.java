/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
import javax.servlet.http.HttpServletResponse;

import filters.ActionValidationFilter;

/**
 * Filter sets everything in the response what could make clients
 * not cache it.
 * 
 * @author Jens Illig
 * @author rja
 */
public class NoCacheFilter implements Filter {
	
	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		
		final HttpServletRequest httpRequest = (HttpServletRequest)request;
		/*
		 * ignore resource files (CSS, JPEG/PNG, JavaScript) ... 
		 */
		if (httpRequest.getServletPath().startsWith(ActionValidationFilter.STATIC_RESOURCES)) {
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
			
		final HttpServletResponse httpResponse = (HttpServletResponse) response;
		httpResponse.setHeader("Pragma","no-cache");
		httpResponse.setHeader("Cache-Control","no-cache");
		httpResponse.setDateHeader("Expires",-1);
		httpResponse.setDateHeader("Last-Modified",0);
	    filterChain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

}
