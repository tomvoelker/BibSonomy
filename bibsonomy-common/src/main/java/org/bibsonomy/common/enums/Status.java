package org.bibsonomy.common.enums;

/**
 * @author Dominik Benz
 * @version $Id$
 */
public enum Status {

	OK("OK"),
	FAIL("Failure");
	
	private final String message;
	
	private Status(final String message) {
		this.message = message;
	}	
	
	public String getMessage() {
		return this.message;
	}	

}