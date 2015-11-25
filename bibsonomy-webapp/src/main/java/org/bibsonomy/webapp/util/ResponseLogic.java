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