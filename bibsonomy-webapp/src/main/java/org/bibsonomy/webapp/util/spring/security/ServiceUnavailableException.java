package org.bibsonomy.webapp.util.spring.security;

import org.springframework.security.core.AuthenticationException;

/**
 * An exception which signalises, that the called service is currently not available.
 * Caller must provide the number of seconds, after which the client may try to call
 * the service again.  
 * 
 * Equivalent to HTTP status code 503 Service Unavailable, see 
 * http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html
 * 
 * @author rja
 * @version $Id$
 */
public class ServiceUnavailableException extends AuthenticationException {

	private static final long serialVersionUID = 1L;
	private final long retryAfter;

	/**
	 * Constructs a new ServiceUnavailableException with the specified detail message.
	 * The cause is not initialized, and may subsequently be initialized by a
	 * call to {@link #initCause(Throwable)}.
	 * 
	 * @param message
	 *            the detail message. The detail message is saved for later
	 *            retrieval by the {@link #getMessage()} method.
	 * @param retryAfter - the number of seconds the client has to wait until the
	 * service is available again. 
	 */
	public ServiceUnavailableException(final String message, final long retryAfter) {
		super(message);
		this.retryAfter = retryAfter;
	}

	/**
	 * @return The number of seconds the client has to wait until the service is 
	 * available again.
	 */
	public long getRetryAfter() {
		return this.retryAfter;
	}
}