package org.bibsonomy.common.exceptions;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class UnsupportedResourceTypeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new unsupported resource type exception with a default
	 * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause(Throwable)}.
     */
	public UnsupportedResourceTypeException() {
		super("Please specify a resource-type by appending '?resourcetype=bibtex' or '?resourcetype=bookmark' to the requested URL");
	}
	
	/**
	 * Constructs a new unsupported resource type exception with the specified
	 * detail message. The cause is not initialized, and may subsequently be
	 * initialized by a call to {@link #initCause(Throwable)}.
     *
     * @param   message   the detail message. The detail message is saved for 
     *          later retrieval by the {@link #getMessage()} method.
     */
	public UnsupportedResourceTypeException(String message) {
		super(message);
	}	
}