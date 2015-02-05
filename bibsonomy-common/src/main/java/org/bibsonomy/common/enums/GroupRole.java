package org.bibsonomy.common.enums;

import java.util.Set;

import org.bibsonomy.util.Sets;

/**
 * TODO: move pending group roles to a separate enum?
 * 
 * Enum for the different group roles.
 * 
 * @author clemensbaier
 */
public enum GroupRole {

	/** administrator */
	ADMINISTRATOR(0),

	/** moderator */
	MODERATOR(1),

	/** user */
	USER(2), // old 7

	/** dummy */
	DUMMY(3), // old 2

	/** user invited by an admin or moderator */
	INVITED(4), // old 3

	/** request to join the group */
	REQUESTED(5); // old 4

	// TODO: Remove these, since unneeded with new GroupRole ids
	/** all pending group roles */
	@Deprecated
	public static final Set<GroupRole> PENDING_GROUP_ROLES = Sets.asSet(GroupRole.INVITED, GroupRole.REQUESTED);

	/** all non pending group roles */
	@Deprecated
	public static final Set<GroupRole> GROUP_ROLES = Sets.asSet(GroupRole.ADMINISTRATOR, GroupRole.MODERATOR, GroupRole.USER);

	/** all group roles with special abilities **/
	@Deprecated
	public static final Set<GroupRole> HIGHER_GROUP_ROLES = Sets.asSet(GroupRole.ADMINISTRATOR, GroupRole.MODERATOR);

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
	 *        - a String representing the object. Must be an integer number.
	 * @return The corresponding object.
	 */
	public static GroupRole getGroupRole(final String level) {
		if (level == null) {
			return USER;
		}
		return getGroupRole(Integer.parseInt(level));
	}

	/**
	 * Creates an instance of this class by its Integer representation.
	 * 
	 * @param level
	 *        - an Integer representing the object.
	 * @return The corresponding object.
	 */
	public static GroupRole getGroupRole(final int level) {
		for (final GroupRole r : GroupRole.values()) {
			if (r.role == level) {
				return r;
			}
		}
		throw new IllegalArgumentException("unknown group role id " + level);
	}

	/**
	 * checks if this role represents a member role.
	 * @return true, if this.role is either an administrator, a moderator or a user.
	 */
	public boolean isMemberRole() {
		return this.role < 3;
	}
	
	/**
	 * checks if this role represents a privileged member role.
	 * @return true, if this.role is either an administrator or a moderator.
	 */
	public boolean isPrivilegedRole() {
		return this.role < 2;
	}

	/**
	 * checks if this role is a pending role (and therefore has no access rights)
	 * @return true if this.role is either invited or requested
	 */
	public boolean isPendingRole() {
		return this.role > 3;
	}
}
