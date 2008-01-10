package org.bibsonomy.webapp.exceptions;

/**
 * Exception to handle malformed URL schemes
 * 
 * @author Dominik Benz
 * @version $Id$
 */
public class MalformedURLSchemeException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructs a new malformed URL scheme exception with the specified
	 * detail message. .
     *
     * @param   message   the detail message. The detail message is saved for 
     *          later retrieval by the {@link #getMessage()} method.
     */
	public MalformedURLSchemeException(final String message) {
		super(message);
	}
}
