package org.bibsonomy.common.errors;


/**
 * Use this ErrorMessage if a Post was to be updated but could not because no original was found
 * 
 * @author sdo
 * @version $Id$
 */
public class UpdatePostErrorMessage extends ErrorMessage {


	/**
	 * @param resourceClassName
	 * @param intraHash
	 */
	public UpdatePostErrorMessage(String resourceClassName, String intraHash) {
		this.setDefaultMessage("Could not update " + resourceClassName + ": This " + resourceClassName +
				" does not exists in your collection (intrahash: " + intraHash + ")");
		this.setErrorCode("database.exception.update.noOriginal");
	}

}
