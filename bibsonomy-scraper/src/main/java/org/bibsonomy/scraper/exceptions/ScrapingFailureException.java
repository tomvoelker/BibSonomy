package org.bibsonomy.scraper.exceptions;


/**
 * Bibtex is not generated as expected (output is  something like "" or null).
 * @author tst
 */
public class ScrapingFailureException extends ScrapingException {

	private static final long serialVersionUID = -5622350446172682574L;

	/**
	 * set message
	 * @param message
	 */
	public ScrapingFailureException(String message) {
		super(message);
	}

	/**
	 * set exception
	 * @param exception
	 */
	public ScrapingFailureException(Exception exception) {
		super(exception);
	}

}
