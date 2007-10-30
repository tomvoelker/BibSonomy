package org.bibsonomy.common.exceptions;

/**
 * Is throw in case of an invalid model, e.g. user object is missing a name.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public class InvalidModelException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
     * Constructs a new invalid model exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause(Throwable)}.
     *
     * @param   message   the detail message. The detail message is saved for 
     *          later retrieval by the {@link #getMessage()} method.
     */
	public InvalidModelException(final String message) {
		super(message);
	}
}