package org.bibsonomy.common.enums;

/**
 * Enum for the different group roles.
 * 
 * @author clemensbaier
 */
public enum GroupRole {
	/** administrator */
	ADMINISTRATOR(0),
	/** user */
	USER(7),
	/** moderator */
	MODERATOR(1),
	/** dummy */
	DUMMY(2),
	/** user invited by an admin or moderator */
	INVITED(3),
	/** request to join the group */
	REQUESTED(4);

	private final int role;

	private GroupRole(final int role) {
		this.role = role;
	}

	/**
	 * Returns the numerical representation of this object.
	 * 
	 * @return The numerical representation of the object.
	 */
	public int getRole() {
		return this.role;
	}

	/**
	 * Creates an instance of this class by its String representation.
	 * 
	 * @param level
	 *            - a String representing the object. Must be an integer number.
	 * @return The corresponding object.
	 */
	public static GroupRole getGroupRole(final String level) {
		if (level == null)
			return USER;
		return getGroupRole(Integer.parseInt(level));
	}

	/**
	 * Creates an instance of this class by its Integer representation.
	 * 
	 * @param level
	 *            - an Integer representing the object.
	 * @return The corresponding object.
	 */
	public static GroupRole getGroupRole(final int level) {
		for (GroupRole r : GroupRole.values()) {
			if (r.role == level) {
				return r;
			}
		}
		throw new IllegalArgumentException("unknown group role id " + level);
	}
	
	public boolean isMemberRole() {
		return this.role == 0 || this.role == 1 || this.role == 7;
	}
	
	public boolean isPendingRole() {
		return this.role == 3 || this.role == 4;
	}
}
