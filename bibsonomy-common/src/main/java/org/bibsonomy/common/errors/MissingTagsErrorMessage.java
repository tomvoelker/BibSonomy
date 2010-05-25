package org.bibsonomy.common.errors;

/**
 * @author sdo
 * @version $Id$
 */
public class MissingTagsErrorMessage extends ErrorMessage {

	/**
	 * public constructor
	 */
	public MissingTagsErrorMessage() {
		this.setDefaultMessage("The post has no tags. A post must have at least one tag.");
		this.setErrorCode("database.exception.missing.tag");
	}

}
