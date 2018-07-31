package org.bibsonomy.common.errors;

/**
 * Error message that can be used if an object can't be found in the system
 * For example if the user wants to update a non-existing resource
 *
 * @author dzo
 */
public class MissingObjectErrorMessage extends ErrorMessage {

	/**
	 * @param id
	 * @param objectType
	 */
	public MissingObjectErrorMessage(final String id, final String objectType) {
		super("Can't find object with id " + id, "error.notfound." + objectType);
	}
}
