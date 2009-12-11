package org.bibsonomy.common.errors;


/**
 * @author sdo
 * @version $Id$
 * Use this ErrorMessage when a post is updated such that it is identical to another already existing post
 */
public class IdenticalHashErrorMessage extends ErrorMessage{

	/**
	 * @param resourceClassName
	 * @param intraHash
	 */
	public IdenticalHashErrorMessage(String resourceClassName, String intraHash) {
		super ();
		this.setErrorMessage("Could not uptdate " + resourceClassName + ": This " + resourceClassName +
		" already exists in your collection (intrahash: " + intraHash + ")");
		this.setLocalizedMessageKey("database.exception.duplicate");
	}
	
}
