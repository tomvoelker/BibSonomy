package org.bibsonomy.webapp.util;

import javax.servlet.http.HttpServletRequest;

import org.bibsonomy.model.User;
import org.tuckey.web.filters.urlrewrite.UrlRewriter;

import filters.ActionValidationFilter;
import filters.InitUserFilter;

/**
 * <p>
 * Provides access to loginUser, format, ckey, validCkey, etc. by extracting
 * this attributes from the request.
 * <br/>
 * Basically, a wrapper/proxy for the request. 
 * </p>
 * 
 * <p>Using this class is only a <em>workaround</em> to transfer request attributes
 * from the filters {@link UrlRewriter}, {@link ActionValidationFilter}, and
 * {@link InitUserFilter} to the command and later to the Views.
 * </p>
 * 
 * @author rja
 * @version $Id$
 */
public class RequestWrapperContext {

	private HttpServletRequest request;

	
	
	/** The request this wrapper provides access to.
	 * 
	 * @param request
	 */
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	/** Returns the logged in user.
	 *  
	 * @return An instance of the logged in user.
	 */
	public User getLoginUser() {
		return InitUserFilter.getUser();
	}
	
	/**
	 * helper function to ease checking if a logged in user exists
	 * @return true if a user is logged in, false otherwise
	 */
	public boolean isUserLoggedIn() {
		return this.getLoginUser().getName() != null;
	}
	
	/**
	 * wrapper for function userLoggedIn to enable access from JSPs
	 * @see #isUserLoggedIn()
	 * @return true if a user is logged in, false otherwise 
	 */
	public boolean getUserLoggedIn() {
		return this.isUserLoggedIn();
	}
	
	/** Delivers a new ckey to be used in forms.
	 * 
	 * @return A ckey. 
	 */
	public String getCkey() {
		return (String) getRequestAttribute(ActionValidationFilter.REQUEST_ATTRIB_CREDENTIAL);
	}
	
	/** Returns <code>true</code> only, when the user request contained a valid ckey.
	 * 
	 * @return <code>true</code>, when the request contained a valid ckey.
	 */
	public boolean isValidCkey() {
		Object isValidCkey = getRequestAttribute(ActionValidationFilter.REQUEST_ATTRIB_VALID_CREDENTIAL);
		return isValidCkey != null && (Boolean) isValidCkey;
	}
	
	private Object getRequestAttribute(final String name) {
		return request.getAttribute(name);
	}
	
	/**
	 * @return The query string of the request.
	 */
	public String getQueryString() {
		return request.getQueryString();
	}
	
	
}
