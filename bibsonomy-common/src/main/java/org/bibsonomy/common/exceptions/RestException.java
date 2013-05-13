package org.bibsonomy.common.exceptions;

/**
 * Exception that can be used to provide error message and response statuscode information to the frontend.
 * 
 * @author Jens Illig
 * @version $Id$
 */
public class RestException extends RuntimeException {
	private static final long serialVersionUID = 7907882646866488962L;
	private final int httpCode;
	private final String messageKey;
	private final String message;
	
	/**
	 * Construct
	 * @param httpCode
	 * @param message
	 * @param messageKey
	 */
	public RestException(int httpCode, String message, String messageKey) {
		this.httpCode = httpCode;
		this.message = message;
		this.messageKey = messageKey;
	}

	/**
	 * @return the httpCode
	 */
	public int getHttpCode() {
		return this.httpCode;
	}

	/**
	 * @return the messageKey
	 */
	public String getMessageKey() {
		return this.messageKey;
	}

	/**
	 * @return the message
	 */
	@Override
	public String getMessage() {
		return this.message;
	}
	
	
}
