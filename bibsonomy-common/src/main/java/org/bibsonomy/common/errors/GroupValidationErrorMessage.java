package org.bibsonomy.common.errors;

/**
 * @author ema
 * @version $Id$
 */
public class GroupValidationErrorMessage extends ErrorMessage{

	/**
	 * @param resourceClassName
	 * @param intraHash
	 */
	public GroupValidationErrorMessage() {
		super ();
		this.setDefaultMessage("At least one of the required attributes of the group, that was to update, was not set: groupId, name, privacyLevel, sharedDocuments");
		this.setErrorCode("settings.group.error.mandatoryFieldsNotSet");
	}
}
