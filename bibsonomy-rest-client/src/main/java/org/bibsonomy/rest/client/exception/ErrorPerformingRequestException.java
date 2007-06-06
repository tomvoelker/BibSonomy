package org.bibsonomy.rest.client.exception;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class ErrorPerformingRequestException extends Exception {

	private static final long serialVersionUID = 1L;

	public ErrorPerformingRequestException(final String message) {
		super(message);
	}

	public ErrorPerformingRequestException(final Throwable cause) {
		super(cause);
	}
}