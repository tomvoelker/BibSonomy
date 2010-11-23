package org.bibsonomy.webapp.util;

import javax.servlet.http.HttpServletRequest;

import org.bibsonomy.model.User;
import org.bibsonomy.webapp.util.spring.security.UserAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import filters.ActionValidationFilter;

/**
 * <p>
 * Provides access to loginUser, format, ckey, validCkey, etc. by extracting
 * this attributes from the request.
 * <br/>
 * Basically, a wrapper/proxy for the request. 
 * </p>
 * 
 * <p>Using this class is only a <em>workaround</em> to transfer request attributes
 * from the filters {@link ActionValidationFilter} to the command and later to the Views.
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
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			final Object principal = authentication.getPrincipal();
			
			if (principal != null && principal instanceof UserAdapter) {
				final UserAdapter adapter = (UserAdapter) principal;
				return adapter.getUser();
			}
		}
		
		return new User();
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
		return getRequestAttribute(ActionValidationFilter.REQUEST_ATTRIB_CREDENTIAL, String.class);
	}
	
	/** Returns <code>true</code> only, when the user request contained a valid ckey.
	 * 
	 * @return <code>true</code>, when the request contained a valid ckey.
	 */
	public boolean isValidCkey() {
		Boolean isValidCkey = getRequestAttribute(ActionValidationFilter.REQUEST_ATTRIB_VALID_CREDENTIAL, Boolean.class);
		return isValidCkey != null && isValidCkey;
	}
	
	@SuppressWarnings("unchecked")
	private <T> T getRequestAttribute(final String name, @SuppressWarnings("unused") final Class<T> clazz) {
		return (T) request.getAttribute(name);
	}
	
	/**
	 * @return The query string of the request.
	 */
	public String getQueryString() {
		return request.getQueryString();
	}
	
	
}
