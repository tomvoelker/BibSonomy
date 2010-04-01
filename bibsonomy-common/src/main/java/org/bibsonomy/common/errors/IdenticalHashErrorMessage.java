package org.bibsonomy.common.errors;


/**
 * Use this ErrorMessage when a post is updated such that it is identical to another already existing posts
 * 
 * @author sdo
 * @version $Id$
 */
public class IdenticalHashErrorMessage extends ErrorMessage {

	/**
	 * @param resourceClassName
	 * @param intraHash
	 */
	public IdenticalHashErrorMessage(String resourceClassName, String intraHash) {
		this.setDefaultMessage("Could not uptdate " + resourceClassName + ": This " + resourceClassName +
		" already exists in your collection (intrahash: " + intraHash + ")");
		this.setErrorCode("database.exception.duplicate");
	}
	
}
