package org.bibsonomy.common.enums;

/**
 * Depicts which party of a user should be updated when calling 
 * the <code>update(...)</code> method in the LogicInterface.
 * 
 * @author cvo
 * @version $Id$
 */
public enum UserUpdateOperation {

	/**
	 * Update all parts of the entity.
	 */
	UPDATE_ALL(0),
	/**
	 * Update only the password of a user.
	 */
	UPDATE_PASSWORD(1),
	/**
	 * Update only the settings of a user.
	 */
	UPDATE_SETTINGS(2),
	/**
	 * Update only the core settings of a user (personal data, like homepage etc.)
	 */
	UPDATE_CORE(3);
	
	private int id;
	
	private UserUpdateOperation(final int userUpdateOperation) {
		this.id = userUpdateOperation;
	}
}
