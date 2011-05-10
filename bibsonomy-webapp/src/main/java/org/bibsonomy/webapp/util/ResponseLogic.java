package org.bibsonomy.webapp.util;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import net.oauth.server.OAuthServlet;

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
	
	/** Handles OAuth exceptions
	 * 
	 * @param e the exception to handle
	 * @param realm OAuth realm
	 * @param sendBody determine whether to send the exception's message text
	 * @throws IOException
	 * @throws ServletException
	 */
    public void handleOAuthException(final Exception e, final String realm, boolean sendBody) throws IOException, ServletException {
        OAuthServlet.handleException(this.response, e, realm, sendBody); 
    }

	/**
	 * XXX: don't change the visibility of this method
	 * if you need the response add a method in this logic delegating the action to the
	 * {@link #response} attribute
	 * 
	 * @return the response
	 */
	HttpServletResponse getResponse() {
		return this.response;
	}
	
}