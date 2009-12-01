package org.bibsonomy.common.enums;

/**
 * @author cvo
 * @version $Id$
 */
public enum GroupUpdateOperation {
	
	/**
	 * Update all parts of the entity.
	 */
	UPDATE_SETTINGS(0),
	/**
	 * Update only the password of a user.
	 */
	ADD_NEW_USER(1);
	
	private int id;
	
	private GroupUpdateOperation(final int groupUpdateOperation) {
		this.id = groupUpdateOperation;
	}
}
