package org.bibsonomy.webapp.util;

import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import net.oauth.OAuthMessage;
import net.oauth.server.OAuthServlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.enums.HttpMethod;
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
	private static final Log log = LogFactory.getLog(RequestLogic.class);
	
	/*
	 * HTTP header definitions
	 */
	private static final String HEADER_REFERER = "Referer";
	private static final String HEADER_X_FORWARDED_FOR = "x-forwarded-for";
	private static final String HEADER_ACCEPT = "accept";
	

	/**
	 * The HTTP request this object is handling.
	 */
	private HttpServletRequest request;

	/**
	 * Default constructor.
	 */
	public RequestLogic() {
		// noop
	}

	/**
	 * Constructor to give the request.
	 * @param request
	 */
	public RequestLogic(final HttpServletRequest request) {
		this.request = request;
	}

	/**
	 * @return the method of the request
	 */
	public String getMethod() {
		return request.getMethod();
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
	public String getHostInetAddress() {
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
		return new RequestContext(this.request).getLocale();
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
	 * @return The User object associated with the logged in user.
	 */
	public User getLoginUser() {
		// TODO: instead of using the RequestWrapperContext we could use the authentication provided in the request
		// but then we must set the user of the context in the minimalistic controller spring wrapper
		// FIXME: IoC break: use user object instead of accessing request
		// FIXME: check password again
		return ((RequestWrapperContext) this.request.getAttribute(RequestWrapperContext.class.getName())).getLoginUser();
	}


	/**
	 * The HTTP request the logic is working on.
	 * 
	 * @param request
	 */
	public void setRequest(final HttpServletRequest request) {
		this.request = request;
	}
	
	/**
	 * @return the http method of the request
	 */
	public HttpMethod getHttpMethod() {
		return HttpMethod.getHttpMethod(this.request.getMethod());
	}

	/**
	 * XXX: don't change the visibility of this method
	 * if you need the request add a method in this logic delegating the action to the
	 * {@link #request} attribute
	 * 
	 * @return the request
	 */
	HttpServletRequest getRequest() {
		return this.request;
	}

	/**
	 * Extract the parts of the given request that are relevant to OAuth.
	 * @param URL 
	 *  
	 * @return the OAuth message
	 */
	public OAuthMessage getOAuthMessage(final String URL) {
		return OAuthServlet.getMessage(this.request, URL);
	}

}
