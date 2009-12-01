package org.bibsonomy.common.enums;

/**
 * @author cvo
 * @version $Id$
 */
public enum GroupUpdateOperation {
	
	/**
	 * Update the settings of a group.
	 */
	UPDATE_SETTINGS(0),
	/**
	 * Adds new user to a group.
	 */
	ADD_NEW_USER(1),
	/**
	 * Update the whole group
	 */
	UPDATE_ALL(2);
	
	private int id;
	
	private GroupUpdateOperation(final int groupUpdateOperation) {
		this.id = groupUpdateOperation;
	}
}
