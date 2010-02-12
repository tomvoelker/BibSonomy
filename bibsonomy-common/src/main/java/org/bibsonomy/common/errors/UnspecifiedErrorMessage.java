package org.bibsonomy.common.errors;


/**
 * @author sdo
 * @version $Id$
 * Use this message if something went wrong and doesn't fit one of the other ErrorMessages.
 * Add the Exception, that occurred.
 */
public class UnspecifiedErrorMessage extends ErrorMessage{

	private final Exception ex;
	/**
	 * @param ex The exception that was caught.
	 */
	public UnspecifiedErrorMessage(Exception ex) {
		this.setErrorMessage(ex.getMessage());
		this.setLocalizedMessageKey("database.exception.unspecified");
		this.setParameters(null);
		this.ex=ex;
	}
	
	/**
	 * return the exception, that caused the error
	 */
	public Exception getException() {
		return ex;
	}
}
