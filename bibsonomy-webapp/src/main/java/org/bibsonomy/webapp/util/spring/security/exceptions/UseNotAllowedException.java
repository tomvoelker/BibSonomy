package org.bibsonomy.webapp.util.spring.security.exceptions;

import org.springframework.security.authentication.AccountStatusException;

/**
 * an exception indicating that the user was authenticated successfully
 * but is not allowed to use the system
 *
 * @author dzo
 */
public class UseNotAllowedException extends AccountStatusException {
	private static final long serialVersionUID = -7463521618700686585L;

	/**
	 * @param msg
	 * @param t
	 */
	public UseNotAllowedException(String msg, Throwable t) {
		super(msg, t);
	}

	/**
	 * @param msg
	 */
	public UseNotAllowedException(String msg) {
		super(msg);
	}
}
