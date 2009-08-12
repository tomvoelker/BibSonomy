package org.bibsonomy.webapp.util;

import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.User;
import org.springframework.web.servlet.support.RequestContext;

/**
 * Provides convenient access to the HTTP request. The request should 
 * never be accessed directly, instead, all methods should be put into
 * this class. 
 * 
 * Nevertheless, try to keep this class as small as possible and try 
 * to re-use or refactor existing methods!
 * 
 * @author rja
 * @version $Id$
 */
public class RequestLogic {
	/*
	 * HTTP header definitions
	 */
	private static final String HEADER_REFERER = "Referer";
	private static final String HEADER_X_FORWARDED_FOR = "x-forwarded-for";
	private static final String HEADER_ACCEPT = "accept";

	private static final Log log = LogFactory.getLog(RequestLogic.class);

	/**
	 * The HTTP request this object is handling.
	 */
	private HttpServletRequest request;

	/**
	 * Default constructor.
	 */
	public RequestLogic() {
		super();
	}
	
	/**
	 * Constructor to give the request.
	 * @param request
	 */
	public RequestLogic(HttpServletRequest request) {
		super();
		this.request = request;
	}

	/**
	 * @return The inet address of the requesting host. Since the system 
	 * typically runs behind a proxy, this is NOT the host address, but 
	 * rather the contents of the x-forwarded-for header. This also contains
	 * all involved proxies, separated by comma.
	 */
	public String getInetAddress() {		
		return request.getHeader(HEADER_X_FORWARDED_FOR);
	}
	
	/** 
	 * Extracts from the list of hosts in the x-forwarded-for header the
	 * first host.
	 * 
	 * FIXME: check for correct implementation!
	 *  
	 * Since we're typically behind a proxy, we have to strip the proxies address.
	 * TODO: Does stripping the proxy work?
	 * 
	 * 
	 * @see #getInetAddress()
	 * @return The extracted address of the host.
	 */
	public String getHostInetAddress () {
		final String inetAddress = getInetAddress();
		if (inetAddress != null) {
			final int proxyStartPos = inetAddress.indexOf(",");
			if (log.isDebugEnabled()) log.debug("inetAddress = " + inetAddress + ", proxyStartPos = " + proxyStartPos);
			if (proxyStartPos > 0) { 
				return inetAddress.substring(0, proxyStartPos);
			}
			if (log.isDebugEnabled()) log.debug("inetAddress = " + inetAddress + " (cutted)");
			return inetAddress;
		}
		return "";
	}

	/**
	 * @return The referer header of the request.
	 */
	public String getReferer() {
		return request.getHeader(HEADER_REFERER);
	}
	
	/**
	 * @return The accept header of the request.
	 */
	public String getAccept() {
		return request.getHeader(HEADER_ACCEPT);
	}

	/**
	 * @return The locale associated with the current request.
	 */
	public Locale getLocale() {
		return new RequestContext(request).getLocale();
	}
	
	/**
	 * @return The cookies contained in the request.
	 */
	public Cookie[] getCookies() {
		return request.getCookies();
	}
	
	/**
	 * Invalidates the current session (i.e., closes it).
	 */
	public void invalidateSession() {
		request.getSession().invalidate();
	}
	
	/** Sets the request attribute <code>key</code> to <code>value</code>. 
	 * 
	 * @param key
	 * @param value
	 */
	public void setSessionAttribute(final String key, final Object value) {
		request.getSession(true).setAttribute(key, value); 
	}
	
	/** Gets a session attribute
	 * 
	 * @param key
	 * @return Object
	 */
	public Object getSessionAttribute(final String key) {
		return request.getSession(true).getAttribute(key);
	}
	
	/**
	 * Gets the HTTP session
	 * @return HTTPSession
	 */
	public HttpSession getSession() {
		return request.getSession();
	}
	
	/**
	 * Gets a paramter from the HTTPRequest
	 * @param parameter name of the parameter
	 * @return value of parameter
	 */
	public String getParameter(final String parameter) {
		return request.getParameter(parameter);
	}
	
	/**
	 * @return Parameter map
	 */
	public Map getParameterMap() {
		return request.getParameterMap();
	}
	
	/**
	 * @return request URL
	 */
	public StringBuffer getRequestURL() {
		return request.getRequestURL();
	}
	
	/**
	 * @return query string
	 */
	public String getQueryString() {
		return request.getQueryString();
	}
	
	/**
	 * @return The User object associated with the logged in user.
	 */
	public User getLoginUser() {
		/*
		 * this method was located in UserFactoryBean ... comments have been transfered.
		 */
		// FIXME: IoC break: use user object instead of accessing request
		// FIXME: use bibsonomy2 user object and check password again
		 return ((RequestWrapperContext) request.getAttribute(RequestWrapperContext.class.getName())).getLoginUser();
	}
	
	/**
	 * @return the context of the request
	 */
	public String getContextPath() {
		return request.getContextPath();		
	}
	
	
	
	/**
	 * The HTTP request the logic is working on.
	 * 
	 * @param request
	 */
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	/**
	 * Builds query string from parameter map
	 * 
	 * @return querystring
	 */
	public String getParametersAsQueryString() {
		StringBuffer buf = new StringBuffer("?");
		try {
			Enumeration paramNames = request.getParameterNames();
			while ( paramNames.hasMoreElements() ) {
			    String param = (String) paramNames.nextElement();
			    buf.append(param + "=" );
			    String paramValues[] = request.getParameterValues(param);
			    if ( paramValues.length == 1 ) {
		            String paramValue = paramValues[0];
		            if ( paramValue.length() == 0 )
		            	buf.append("");
					else
							buf.append(paramValue);
			    } else {
		            for (int i = 0; i < paramValues.length; i++ )
		            	buf.append(paramValues[i] + "%20" ) ;
		        }
			    buf.append("&");
		    }
		} catch (Exception ex) {
			log.warn("Could not build query string.", ex);
		}
		return buf.toString();
	}
		
}
