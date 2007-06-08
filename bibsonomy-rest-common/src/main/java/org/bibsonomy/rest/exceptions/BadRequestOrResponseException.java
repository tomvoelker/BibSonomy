package org.bibsonomy.rest.exceptions;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class BadRequestOrResponseException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public BadRequestOrResponseException(final String message) {
		super(message);
	}

	public BadRequestOrResponseException(final Throwable cause) {
		super(cause);
	}
}