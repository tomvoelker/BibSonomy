package org.bibsonomy.rest.exceptions;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class NoSuchResourceException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NoSuchResourceException(final String message) {
		super(message);
	}
}