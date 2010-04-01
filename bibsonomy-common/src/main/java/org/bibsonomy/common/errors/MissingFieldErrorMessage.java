package org.bibsonomy.common.errors;

/**
 * Use this Message when a post is incomplete (vital fields are missing)
 * Localized Message for Missing: Groups and Resource
 * 
 * @author sdo
 * @version $Id$
 */
public class MissingFieldErrorMessage extends ErrorMessage{

	/**
	 * @param missing 
	 */
	public MissingFieldErrorMessage(String missing) {
		this.setDefaultMessage("Missing "+missing+ " for this post.");
		this.setErrorCode("database.exception.missing."+missing.toLowerCase());
	}
}
