package org.bibsonomy.common.errors;


/**
 * Use this message if something went wrong with a SystemTag
 * 
 * @author sdo
 * @version $Id$
 */
public class SystemTagErrorMessage extends ErrorMessage{

	/**
	 * @param errorMessage
	 * @param localizedMessageKey
	 * @param parameters
	 */
	public SystemTagErrorMessage(String errorMessage, String localizedMessageKey, String[] parameters) {
		this.setDefaultMessage(errorMessage);
		this.setErrorCode(localizedMessageKey);
		this.setParameters(parameters);
	}
}
