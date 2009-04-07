package org.bibsonomy.webapp.exceptions;

/** An exception which signalises, that the requested content is not available
 * in the accepted formats of the client, as specified by the "Accept" header. 
 * 
 * Equivalent to HTTP status code 406 Not Acceptable, see 
 * http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html
 * 
 * @author rja
 * @version $Id$
 */
public class NotAcceptableException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private String[] acceptableContentTypes;

	/**
	 * Constructs a new NotAcceptableException with the specified detail message.
	 * The cause is not initialized, and may subsequently be initialized by a
	 * call to {@link #initCause(Throwable)}.
	 * 
	 * @param message
	 *            the detail message. The detail message is saved for later
	 *            retrieval by the {@link #getMessage()} method.
	 * @param acceptableContentTypes 
	 * 			  an array containing all acceptable content types
	 *            
	 */
	public NotAcceptableException(final String message, final String[] acceptableContentTypes) {
		super(message);
		this.acceptableContentTypes = acceptableContentTypes;
	}

	/**
	 * @return The list of acceptable content types.
	 */
	public String[] getAcceptableContentTypes() {
		return this.acceptableContentTypes;
	}

}