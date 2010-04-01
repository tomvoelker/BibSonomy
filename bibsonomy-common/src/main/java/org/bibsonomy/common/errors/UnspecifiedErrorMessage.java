package org.bibsonomy.common.errors;

/**
 * Use this message if something went wrong and doesn't fit one of the other ErrorMessages.
 * Add the Exception, that occurred.
 * 
 * @author sdo
 * @version $Id$
 */
public class UnspecifiedErrorMessage extends ErrorMessage {
	private final Exception ex;
	
	/**
	 * @param ex The exception that was caught.
	 */
	public UnspecifiedErrorMessage(Exception ex) {
		this.setDefaultMessage(ex.getMessage());
		this.setErrorCode("database.exception.unspecified");
		this.setParameters(null);
		this.ex = ex;
	}
	
	/**
	 * @return the exception, that caused the error
	 */
	public Exception getException() {
		return ex;
	}
}
