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
package org.bibsonomy.webapp.util;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.util.UrlBuilder;
import org.bibsonomy.webapp.util.spring.security.saml.context.RelayStateSamlContextProviderImpl;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.web.servlet.support.RequestContext;

import net.oauth.OAuthMessage;
import net.oauth.server.OAuthServlet;

/**
 * Provides convenient access to the HTTP request. The request should 
 * never be accessed directly, instead, all methods should be put into
 * this class. 
 * 
 * Nevertheless, try to keep this class as small as possible and try 
 * to re-use or refactor existing methods!
 * 
 * @author rja
 */
public class RequestLogic {
	private static final Log log = LogFactory.getLog(RequestLogic.class);
	
	/*
	 * HTTP header definitions
	 */
	private static final String HEADER_REFERER = "Referer";
	private static final String HEADER_X_FORWARDED_FOR = "x-forwarded-for";
	private static final String HEADER_ACCEPT = "accept";
	private static final String LAST_ACTION_SESSION_KEY = "lastAction";
	

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
	 * Removes a session attribute
	 * 
	 * @param key the key of the attribute to be removed
	 */
	public void removeSessionAttribute(final String key) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return;
		}
		session.removeAttribute(key);
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

	/**
	 * Sets the relaystate that is to be send by the next SAML request.
	 * The relaystate is a parameter that is required by by the SAML standard to be send back to the SP when the IdP receives it from the SP.
	 * It allows identification of responses to a particular authentication request.
	 * @param value
	 */
	public void setNextRelayState(String value) {
		this.request.setAttribute(RelayStateSamlContextProviderImpl.SAML_RELAYSTATE_ATTR_NAME, value);
	}
	
	/**
	 * @return the relaystate parameter send back from SAML IdPs
	 */
	public String getRelayState() {
		return this.request.getParameter("RelayState");
	}
	
	/**
	 * @return a new UrlBuilder with the currently requested url
	 */
	public UrlBuilder getUrlBuilder() {
		UrlBuilder urlb = new UrlBuilder(UrlUtils.buildFullRequestUrl(request.getScheme(), request.getServerName(), request.getServerPort(), request.getRequestURI(), null));
		for (Map.Entry<String, String[]> param : request.getParameterMap().entrySet()) {
			urlb.addParameter(param.getKey(), param.getValue()[0]);
		}
		return urlb;
	}

	/**
	 * simply creates a session if there is none yet
	 */
	public void ensureSession() {
		request.getSession(true);
	}
	
	@Deprecated // Use flash messages TODO: remove
	public void setLastAction(String lastAction) {
		setSessionAttribute(LAST_ACTION_SESSION_KEY, lastAction);
	}
	
	@Deprecated // Use flash messages TODO: remove
	public String getLastAction() {
		return (String) getSessionAttribute(LAST_ACTION_SESSION_KEY);
	}
}
