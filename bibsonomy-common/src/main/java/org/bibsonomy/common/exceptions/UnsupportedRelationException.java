package org.bibsonomy.common.exceptions;

/**
 * @author sdo
 * @version $Id$
 */
public class UnsupportedRelationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new unsupported relation exception with a default
	 * detail message. The cause is not initialized, and may subsequently be
	 * initialized by a call to {@link #initCause(Throwable)}.
	 */
	public UnsupportedRelationException() {
		super("The relation can not be processed by this method");
	}

	/**
	 * Constructs a new unsupported relation exception with the specified
	 * detail message. The cause is not initialized, and may subsequently be
	 * initialized by a call to {@link #initCause(Throwable)}.
	 * 
	 * @param message
	 *            the detail message. The detail message is saved for later
	 *            retrieval by the {@link #getMessage()} method.
	 */
	public UnsupportedRelationException(String message) {
		super(message);
	}

}
