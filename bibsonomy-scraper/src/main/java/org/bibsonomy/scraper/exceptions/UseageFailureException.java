package org.bibsonomy.scraper.exceptions;


/**
 * Failure because of unsupported actions from user. This failure can be solved 
 * with a little trick by the user (trick is explained in the exception message).
 * @author tst
 */
public class UseageFailureException extends ScrapingException {

	private static final long serialVersionUID = -4269129145897321143L;

	/**
	 * set message
	 * @param message
	 */
	public UseageFailureException(String message) {
		super(message);
	}

	/**
	 * set exception
	 * @param exception
	 */
	public UseageFailureException(Exception exception) {
		super(exception);
	}

}
