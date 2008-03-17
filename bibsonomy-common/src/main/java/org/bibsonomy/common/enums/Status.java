package org.bibsonomy.common.enums;

/**
 * Some statuses that may indicate success or failure of a certain job.
 * 
 * @author Dominik Benz
 * @version $Id$
 */
public enum Status {
	/** everything is ok */
	OK("OK"),
	/** something went wrong */
	FAIL("Failure");

	private final String message;

	private Status(final String message) {
		this.message = message;
	}

	/**
	 * @return a string message describing the state
	 */
	public String getMessage() {
		return this.message;
	}
}