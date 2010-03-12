package org.bibsonomy.common.errors;


/**
 * @author sdo
 * @version $Id$
 * Use this message if something went wrong with a SystemTag
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
