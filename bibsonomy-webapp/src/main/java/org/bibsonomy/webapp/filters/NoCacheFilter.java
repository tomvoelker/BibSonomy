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
import javax.servlet.http.HttpServletResponse;

/**
 * Filter sets everything in the response what could make clients
 * not cache it.
 * 
 * @author Jens Illig
 */
public class NoCacheFilter implements Filter {

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		httpResponse.setHeader("Pragma","no-cache");
		httpResponse.setHeader("Cache-Control","no-cache");
		httpResponse.setDateHeader("Expires",-1);
		httpResponse.setDateHeader("Last-Modified",0);
	    filterChain.doFilter(request, response);
	}

	public void init(FilterConfig arg0) throws ServletException {
	}

}
