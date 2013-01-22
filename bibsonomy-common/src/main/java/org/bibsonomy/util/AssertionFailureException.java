package org.bibsonomy.util;

/**
 * Thrown when an assertion such as in {@link ValidationUtils#assertTrue(boolean)} fails
 * 
 * @author jensi
 * @version $Id$
 */
public class AssertionFailureException extends RuntimeException {
	private static final long serialVersionUID = 3214830183612865838L;

	/**
	 * default constructor
	 */
	public AssertionFailureException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * constructor with message
	 * @param message
	 */
	public AssertionFailureException(String message) {
		super(message);
	}
}
