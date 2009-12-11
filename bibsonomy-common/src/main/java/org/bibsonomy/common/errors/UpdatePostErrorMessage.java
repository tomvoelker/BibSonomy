package org.bibsonomy.common.errors;


/**
 * @author sdo
 * @version $Id$
 * 
 * Use this ErrorMessage if a Post was to be updated but could not because no original was found
 */
public class UpdatePostErrorMessage extends ErrorMessage{


	/**
	 * @param resourceClassName
	 * @param intraHash
	 */
	public UpdatePostErrorMessage(String resourceClassName, String intraHash) {
		super();
		this.setErrorMessage("Could not update " + resourceClassName + ": This " + resourceClassName +
				" does not exists in your collection (intrahash: " + intraHash + ")");
		this.setLocalizedMessageKey("database.exception.update.noOriginal");
	}

}
