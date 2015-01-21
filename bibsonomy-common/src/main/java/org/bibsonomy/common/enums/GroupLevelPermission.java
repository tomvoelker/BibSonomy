package org.bibsonomy.common.enums;

/**
 * An enum for permissions that are assigned on the group level.
 * If a groupLevelPermission is assigned to a group, then all members of that
 * group will have that permission.
 * 
 * @author sdo
 */
public enum GroupLevelPermission {
	/** Is allowed to mark community posts as inspected */
	COMMUNITY_POST_INSPECTION(0),
	
	/** A place holder for testing sets of such permissions. To be removed as soon as we add real further group level permissions.
	NOTHING(1);

	/*
	 * TODO: further roles like
	 * * admin
	 * * spam flagger
	 * * discussion moderator
	 */

	private final int groupLevelPermissionId;

	/**
	 * Create a GroupLevelPermission with a given groupLevelPermissionId
	 */
	private GroupLevelPermission(final int groupLevelPermissionId) {
		this.groupLevelPermissionId = groupLevelPermissionId;
	}

	/**
	 * Returns the numerical representation of this object.
	 * 
	 * @return The numerical representation of the object.
	 */
	public int getGroupLevelPermissionId() {
		return this.groupLevelPermissionId;
	}

	/**
	 * Creates an instance of this class by its String representation.
	 * 
	 * @param groupLevelPermissionIdString -
	 *        a String representing the object. Must be an integer number.
	 * @return The corresponding object.
	 */
	public static GroupLevelPermission getGroupLevelPermission(final String groupLevelPermissionIdString) {
		if (groupLevelPermissionIdString == null) {
			throw new IllegalArgumentException("the specified groupLevelPermission must be String representation of an Integer but was " + groupLevelPermissionIdString + ".");
		}
		return getGroupLevelPermission(Integer.parseInt(groupLevelPermissionIdString));
	}

	/**
	 * Creates an instance of this class by its Integer representation.
	 * 
	 * @param groupLevelPermissionId -
	 *        an Integer representing the object.
	 * @return The corresponding object.
	 */
	public static GroupLevelPermission getGroupLevelPermission(final int groupLevelPermissionId) {
		for (final GroupLevelPermission glp : GroupLevelPermission.values()) {
			if (glp.groupLevelPermissionId == groupLevelPermissionId) {
				return glp;
			}
		}
		throw new IllegalArgumentException("unknown groupLevelPermissionId id " + groupLevelPermissionId);
	}

}
