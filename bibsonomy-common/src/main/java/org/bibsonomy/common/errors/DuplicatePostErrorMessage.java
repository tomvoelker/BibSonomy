package org.bibsonomy.common.errors;


/**
 * @author sdo
 * @version $Id$
 */
public class DuplicatePostErrorMessage extends ErrorMessage{

	/**
	 * @param resourceClassName
	 * @param intraHash
	 */
	public DuplicatePostErrorMessage(String resourceClassName, String intraHash) {
		this.setErrorMessage("Could not create new " + resourceClassName + ": This " + resourceClassName +
		" already exists in your collection (intrahash: " + intraHash + ")");
		this.setLocalizedMessageKey("database.exception.duplicate");
	}
	
}
