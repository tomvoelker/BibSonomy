package org.bibsonomy.common.exceptions;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class InternServerException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InternServerException(final String message) {
		super(message);
	}

	public InternServerException(final Throwable cause) {
		super(cause);
	}
}