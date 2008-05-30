package org.bibsonomy.scraper.exceptions;


/**
 * Some other Exception is occured (like Connection,NullPointer,I/O...)
 * @author tst
 */
public class InternalFailureException extends ScrapingException {

	private static final long serialVersionUID = 8287000807631708566L;

	/**
	 * set message
	 * @param message
	 */
	public InternalFailureException(String message) {
		super(message);
	}

	/**
	 * set exception
	 * @param exception
	 */
	public InternalFailureException(Exception exception) {
		super(exception);
	}

}
