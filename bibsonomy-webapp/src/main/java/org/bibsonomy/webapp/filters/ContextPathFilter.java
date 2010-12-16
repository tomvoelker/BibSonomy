package org.bibsonomy.webapp.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Removes the context path from the request. 
 * 
 * @author rja
 * @version $Id$
 */
public class ContextPathFilter implements Filter {
	
	protected final Log log = LogFactory.getLog(ContextPathFilter.class);
	
    /**
     * Instances of this class ignore the context path of the application. I.e., 
     * calls to getRequestURL() & Co. return the URL without the context path.
     * 
     * This is necessary for our setting where we have the Tomcat running behind
     * an Apache Proxy where the application does not have a context path. 
     * 
     * @author rja
     *
     */
    protected static final class ContextPathFreeRequest extends HttpServletRequestWrapper {

		public ContextPathFreeRequest(HttpServletRequest request) {
			super(request);
		}
		
		/**
		 * Modified to return the request URL without the context path. 
		 * 
		 * @see javax.servlet.http.HttpServletRequestWrapper#getRequestURL()
		 */
		@Override
		public StringBuffer getRequestURL() {
			return stripContextPath(super.getRequestURL(), super.getContextPath());
		}
		
		@Override
		public String getRequestURI() {
			return stripContextPath(super.getRequestURI(), super.getContextPath());
		}

		/**
		 * Always returns "".
		 * 
		 * @see javax.servlet.http.HttpServletRequestWrapper#getContextPath()
		 */
		@Override
		public String getContextPath() {
			return "";
		}
		
		/**
		 * 
		 * @param url
		 * @param contextPath
		 * @return
		 */
		protected StringBuffer stripContextPath(final StringBuffer url, final String contextPath) {
			final int indexOf = url.indexOf(contextPath);
			return url.replace(indexOf, indexOf + contextPath.length(), "");
		}
		
		protected String stripContextPath(final String url, final String contextPath) {
			final int indexOf = url.indexOf(contextPath);
			return url.substring(0, indexOf) + url.substring(indexOf + contextPath.length());
		}
    }
	
    protected static final class LoggingResponse extends HttpServletResponseWrapper {
    	protected final Log log = LogFactory.getLog(LoggingResponse.class);
    	
		public LoggingResponse(HttpServletResponse response) {
			super(response);
		}
		
		@Override
		public void addCookie(Cookie cookie) {
			log.debug("adding cookie " + cookie.getName() + ": " + cookie.getValue() + " with path " + cookie.getPath());
			super.addCookie(cookie);
		}
    }
    
	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		/*
		 * If we have HTTP servlet request we wrap it into our own class to 
		 * modify certain calls which the context path.
		 */
		if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
			chain.doFilter(new ContextPathFreeRequest((HttpServletRequest) request), new LoggingResponse((HttpServletResponse) response));	
		} else {
			chain.doFilter(request, response);
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}
}
