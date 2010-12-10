package org.bibsonomy.webapp.exceptions;

/**
 * thrown when an invalid password reminder hash has been sent.
 * 
 * @author Dominik Benz
 * @version $Id$
 */
public class InvalidPasswordReminderException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructor
	 */
	public InvalidPasswordReminderException() {
		super();
	}
}
