package org.bibsonomy.common.enums;

import static org.bibsonomy.util.ValidationUtils.present;

/**
 * Constant group ids.
 */
public enum GroupID {
	/** the public group */
	PUBLIC(0),
	/** the owning user's private group */
	PRIVATE(1),
	/** the owning user's friends group */
	FRIENDS(2),
	/** */
	MULTIPLE(1000000),
	/**
	 * the kde group (normally groups are not hardoded as this, but this is an
	 * example used in some tescases)
	 */
	KDE(3),
	/** an invalid value */
	INVALID(-1),
	
	/**
	 * group for admins to be able to view spam posts
	 */
	ADMINSPAM(-2147483648);

	private final int id;

	private GroupID(final int id) {
		this.id = id;
	}

	/**
	 * @return the constant value behind the symbol
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * @param groupName
	 *            the groupname to look up
	 * @return GroupID representation of a special group which name correspond
	 *         to the argument
	 */
	public static GroupID getSpecialGroup(final String groupName) {
		if (present(groupName) == false) return null;
		final GroupID group = valueOf(groupName.toUpperCase());
		if (isSpecialGroupId(group.getId())) return group;
		return null;
	}

	/**
	 * categorizes groupIds between special and nonspecial groups. special
	 * groups are groups, that are not created by users.
	 * 
	 * @param groupId
	 *            the groupId to check
	 * @return true if the groupId argument is a special group
	 */
	public static boolean isSpecialGroupId(final int groupId) {
		return ((groupId < 3) && (groupId >= 0));
	}

	/**
	 * categorizes groupIds between special and nonspecial groups. special
	 * groups are groups, that are not created by users.
	 * 
	 * @param groupId
	 *            the groupId to check
	 * @return true if the groupId argument is a special group
	 */
	public static boolean isSpecialGroupId(final GroupID groupId) {
		return isSpecialGroupId(groupId.getId());
	}

	/**
	 * wrapper function to check if a given groupname represents a special group
	 * 
	 * @param groupName
	 * @return true if the given group is a special group, false otherwise
	 */
	public static boolean isSpecialGroup(final String groupName) {
		try {
			if (getSpecialGroup(groupName) != null) return true;
		} catch (IllegalArgumentException ignore) {
		}
		return false;
	}
}