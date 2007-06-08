package org.bibsonomy.common.exceptions;

/**
 * Is throw in case of an invalid model, e.g. user object is missing a name.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public class InvalidModelException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidModelException(final String message) {
		super(message);
	}
}