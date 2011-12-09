package org.bibsonomy.webapp.filters;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    	
    	// these tree variables can be removed when getHeader has been removed (dbe)
		private static final String AUTH_HEADER = "authorization";
		private static final String USER_AGENT_HEADER = "user-agent";
		private static final Pattern TYPO3_USER_AGENT_REGEX = Pattern.compile("HTTP_Request2\\/0\\.5\\.1");

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
		
		/**
		 * This is a TEMPORARY fix to disable HTTP basic authorization from requests which stem
		 * from the Typo3-plugin (which uses PEARs HTTP client, see 
		 * http://pear.php.net/package/HTTP_Request2/docs/latest/HTTP_Request2/HTTP_Request2.html).
		 * 
		 * Please REMOVE as soon as the Typo3 plugin has been updated.
		 *   dbe, 2011-11-08
		 */
		@Override
		public String getHeader(final String name) {
			/*
			 * when asking for another header than authHeader, use the original request
			 */
			if (! (AUTH_HEADER.equalsIgnoreCase(name)) ) {
				return super.getHeader(name);
			}
			
			/*
			 * this means we have getHeader("authorization").
			 * 
			 * check if the request comes from the typo3 plugin. if yes, "remove" the authorization
			 * header by returning null
			 */
			try {
				final String userAgent = super.getHeader(USER_AGENT_HEADER);
				final Matcher m = TYPO3_USER_AGENT_REGEX.matcher(userAgent);
				if (m.find()) {
					return null;
				}
			}
			catch (NullPointerException npe) {
				// ignore silently; could happen if e.g. no user-agent header is present
			}			
			return super.getHeader(AUTH_HEADER);
		}
				
    }
	
    /**
     * Can be used to log calls to the response.
     * 
     * @author rja
     *
     */
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
		 * If we have an HTTP servlet request we wrap it into our own class to 
		 * modify certain calls whose results could contain the context path.
		 */
		try {
			if (request instanceof HttpServletRequest) {
				chain.doFilter(new ContextPathFreeRequest((HttpServletRequest) request), response);	
			} else {
				chain.doFilter(request, response);
			}
		}
		catch (Exception ex) {
			log.error("Error during filter execution.", ex);
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}
}
