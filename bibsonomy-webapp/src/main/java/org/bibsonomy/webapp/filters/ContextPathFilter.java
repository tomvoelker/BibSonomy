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
import java.net.MalformedURLException;
import java.net.URL;
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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.web.util.UrlUtils;

/**
 * Removes the context path from the request. 
 * 
 * @author rja
 */
public class ContextPathFilter implements Filter {
	private static final Log log = LogFactory.getLog(ContextPathFilter.class);
	
	/** the project home */
	private String projectHomeUrl;
	
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

		private static final String AUTH_HEADER = "authorization";
		private static final String USER_AGENT_HEADER = "user-agent";
		private static final Pattern TYPO3_USER_AGENT_REGEX = Pattern.compile("HTTP_Request2\\/0\\.5\\.1");
		private final URL projectHomeUrl;

		public ContextPathFreeRequest(final HttpServletRequest request, final String projectHomeUrl) {
			super(request);
			try {
				this.projectHomeUrl = new URL(projectHomeUrl);
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
		}
		
		/**
		 * Modified to return the request URL without the context path. 
		 * 
		 * @see javax.servlet.http.HttpServletRequestWrapper#getRequestURL()
		 */
		@Override
		public StringBuffer getRequestURL() {
			return new StringBuffer(UrlUtils.buildFullRequestUrl(getScheme(), getServerName(), getServerPort(), getRequestURI(), getQueryString()));
			//return this.stripContextPath(super.getRequestURL(), super.getContextPath());
		}
		
		@Override
		public String getRequestURI() {
			return this.stripContextPath(super.getRequestURI(), super.getContextPath());
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
		 * TODO: This is a TEMPORARY fix to disable HTTP basic authorization from requests which stem
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
			if (!(AUTH_HEADER.equalsIgnoreCase(name)) ) {
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
			} catch (final NullPointerException npe) {
				// ignore silently; could happen if e.g. no user-agent header is present
			}			
			return super.getHeader(AUTH_HEADER);
		}
		
		/* (non-Javadoc)
		 * @see javax.servlet.ServletRequestWrapper#getScheme()
		 */
		@Override
		public String getScheme() {
			return projectHomeUrl.getProtocol();
		}
		
		/* (non-Javadoc)
		 * @see javax.servlet.ServletRequestWrapper#getServerName()
		 */
		@Override
		public String getServerName() {
			return projectHomeUrl.getHost();
		}
		
		/* (non-Javadoc)
		 * @see javax.servlet.ServletRequestWrapper#getServerPort()
		 */
		@Override
		public int getServerPort() {
			final int port = projectHomeUrl.getPort();
			if (port <= 0) {
				if (getScheme().equalsIgnoreCase("https")) {
					return 443;
				}
				return 80;
			}
			return port;
		}
				
	}

	/**
	 * Can be used to log calls to the response.
	 * 
	 * @author rja
	 * 
	 */
	protected static final class LoggingResponse extends HttpServletResponseWrapper {
		private static final Log LOG = LogFactory.getLog(LoggingResponse.class);
		
		/**
		 * @param response
		 */
		public LoggingResponse(final HttpServletResponse response) {
			super(response);
		}
		
		@Override
		public void addCookie(final Cookie cookie) {
			LOG.debug("adding cookie " + cookie.getName() + ": " + cookie.getValue() + " with path " + cookie.getPath());
			super.addCookie(cookie);
		}
	}
	
	/**
	 * sends redirects to the the ${project.home} url. (as configured in project.properties)
	 * 
	 * @author jil
	 * 
	 */
	protected static final class RedirectResolvingResponseWrapper extends HttpServletResponseWrapper {
		
		private final String projectHomeUrl;
		
		/**
		 * @param response
		 * @param projectHomeUrl
		 */
		public RedirectResolvingResponseWrapper(HttpServletResponse response, String projectHomeUrl) {
			super(response);
			this.projectHomeUrl = projectHomeUrl;
		}
		
		/* (non-Javadoc)
		 * @see javax.servlet.http.HttpServletResponseWrapper#sendRedirect(java.lang.String)
		 */
		@Override
		public void sendRedirect(String location) throws IOException {
			if ((projectHomeUrl != null) && StringUtils.startsWith(location, "/") && !StringUtils.startsWith(location, "//")) {
				super.sendRedirect(projectHomeUrl + location);
			} else {
				// TODO: non-absolute paths should be captured as well
				super.sendRedirect(location);
			}
		}
	}
	
	@Override
	public void destroy() {
		// noop
	}

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
		/*
		 * If we have an HTTP servlet request we wrap it into our own class to 
		 * modify certain calls whose results could contain the context path.
		 */
		try {
			if (request instanceof HttpServletRequest) {
				chain.doFilter(new ContextPathFreeRequest((HttpServletRequest) request, this.projectHomeUrl), wrapResponse(response));	
			} else {
				chain.doFilter(request, wrapResponse(response));
			}
		} catch (final Exception ex) {
			final HttpServletRequest castedRequest = (HttpServletRequest)request;
			String requestedUrlWithParams = castedRequest.getRequestURI();
			final String queryString = castedRequest.getQueryString();
			if (present(queryString)) {
				requestedUrlWithParams = "?" + queryString;
			}
			log.error("Error during filter execution. " + requestedUrlWithParams + " with referer " + castedRequest.getHeader("Referer"), ex);
			/*
			 * TODO: It would be nice if we could throw this exception in a way that we see it on a nice error page. Instead of the default Exception Screen.
			 */
			throw new RuntimeException(ex);
		}
	}

	/**
	 * @param response
	 * @return
	 */
	private ServletResponse wrapResponse(ServletResponse response) {
		if (response instanceof HttpServletResponse) {
			return new RedirectResolvingResponseWrapper((HttpServletResponse) response, this.projectHomeUrl);
		}
		return response; 
	}

	@Override
	public void init(final FilterConfig filterConfig) throws ServletException {
		// noop
	}

	/**
	 * called by spring on another object, but sets a static field so that the value will be available in a web.xml filter lining outside the spring-managed world
	 * @param projectHomeUrl
	 */
	public void setProjectHomeUrl(String projectHomeUrl) {
		if (projectHomeUrl.endsWith("/")) {
			projectHomeUrl = projectHomeUrl.substring(0, projectHomeUrl.length() - 1);
		}
		this.projectHomeUrl = projectHomeUrl;
	}
}
