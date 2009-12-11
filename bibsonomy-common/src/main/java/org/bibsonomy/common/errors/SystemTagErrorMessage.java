package org.bibsonomy.common.errors;

import java.util.ArrayList;

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
	public SystemTagErrorMessage(String errorMessage, String localizedMessageKey, ArrayList<String>parameters) {
		this.setErrorMessage(errorMessage);
		this.setLocalizedMessageKey(localizedMessageKey);
		this.setParameters(parameters);
	}
}
