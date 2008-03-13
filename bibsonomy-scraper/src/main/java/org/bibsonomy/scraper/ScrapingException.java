package org.bibsonomy.scraper;

/**
 * If a scraper throws an exception it should inherit this exception or throw this exception.
 *
 */
public class ScrapingException extends Exception {

	private static final long serialVersionUID = -5322213549739868471L;

	/** Set the exception message.
	 * @param message
	 */
	public ScrapingException(final String message) {
		super(message);
	}
	
	/** Include another exception.
	 * @param exception
	 */
	public ScrapingException(final Exception exception) {
		super(exception);
	}
}
