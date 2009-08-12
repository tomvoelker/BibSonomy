/*
 * Created on 12.10.2007
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Filter sets everything in the response what could make clients
 * not cache it.
 * 
 * @author Jens Illig
 */
public class NoCacheFilter implements Filter {

	private final static Log log = LogFactory.getLog(NoCacheFilter.class);
	
	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		
		/*
		 * FIXME: workaround for IE6 bug 
		 * http://www.somacon.com/p106.php
		 * http://www.brookes.ac.uk/mediaworkshop/brookesvirtual/faqs.html#cache
		 */
		if (request.isSecure()) {
			final HttpServletRequest httpRequest = (HttpServletRequest) request;
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

	public void init(FilterConfig arg0) throws ServletException {
	}

}
