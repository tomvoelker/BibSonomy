package org.bibsonomy.scraper.exceptions;


/**
 * Special failure case: Scraper not support the given Page
 * @author tst
 */
public class PageNotSupportedException extends ScrapingException {

	private static final long serialVersionUID = 9030796354343329688L;
	
	/**
	 * default error message
	 */
	public static final String DEFAULT_ERROR_MESSAGE = "The posted page is not supported by scraper ";

	/**
	 * set message
	 * @param message
	 */
	public PageNotSupportedException(String message){
		super(message);
	}
	
	/**
	 * set exception
	 * @param exception
	 */
	public PageNotSupportedException(Exception exception){
		super(exception);
	}
	
}
