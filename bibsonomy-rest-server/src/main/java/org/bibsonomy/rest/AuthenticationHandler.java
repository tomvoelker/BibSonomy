package org.bibsonomy.rest;

import javax.servlet.http.HttpServletRequest;

import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.rest.exceptions.AuthenticationException;

/**
 * interface the handle authentication with the rest server
 *
 * @author dzo
 * @param <T> 
 */
public interface AuthenticationHandler<T> {
	/** the no auth error message */
	public static final String NO_AUTH_ERROR = "Please authenticate yourself.";

	/**
	 * @param authentication
	 * @return <code>true</code> iff the authentication handler can authenticate
	 * the user with the extracted Authentication
	 */
	public boolean canAuthenticateUser(final T authentication);
	
	/**
	 * @param request
	 * @return the extracted authentication object from the request used by the
	 * other methods
	 */
	public T extractAuthentication(final HttpServletRequest request);
	
	/**
	 * authenticats the user based on the extracted authentication
	 * @param authentication
	 * @return the logic for the user
	 * @throws AuthenticationException if the user can not be authenticated
	 */
	public LogicInterface authenticateUser(final T authentication) throws AuthenticationException;
}
