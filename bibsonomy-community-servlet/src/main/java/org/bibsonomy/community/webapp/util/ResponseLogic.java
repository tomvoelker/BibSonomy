package org.bibsonomy.community.webapp.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * Logic to access the Response.
 * 
 * @author rja
 * @version $Id$
 */
public class ResponseLogic {

	private HttpServletResponse response;
	
	/**
	 * Default constructor.
	 */
	public ResponseLogic() {
		super();
	}
	
	/** Constructor to set response
	 * @param response
	 */
	public ResponseLogic(HttpServletResponse response) {
		super();
		this.response = response;
	}

	/** Adds a cookie to the response.
	 * 
	 * @param cookie
	 */
	public void addCookie(Cookie cookie) {
		response.addCookie(cookie);
	}
	
	/** Response this logic is working on.
	 * @param response
	 */
	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}
	
	/** Sets the HTTP status code.
	 * 
	 * @param status
	 */
	public void setHttpStatus(final int status) {
		this.response.setStatus(status);
	}
	
}