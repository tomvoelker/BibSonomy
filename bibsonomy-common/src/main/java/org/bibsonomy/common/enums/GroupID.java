package org.bibsonomy.common.enums;

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
	/** the kde group (normally groups are not hardoded as this, but this is an example used in some tescases) */
	KDE(3),
	/** an invalid value */
	INVALID(-1);

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
	 * @param name the groupname to look up
	 * @return GroupID representation of a special group which name correspond to the argument 
	 */
	public static GroupID getSpecialGroup(final String name) {
		final GroupID group = valueOf(name.toUpperCase());
		if (isSpecialGroupId(group.id)) return group;
		return null;
	}

	/**
	 * categorizes groupIds between special and nonspecial groups. special groups are groups, that are not created by users.
	 * @param groupId the groupId to check
	 * @return true if the groupId argument is a special group
	 */
	public static boolean isSpecialGroupId(final int groupId) {
		return ((groupId < 3) && (groupId >= 0));
	}
	
	/**
	 * categorizes groupIds between special and nonspecial groups. special groups are groups, that are not created by users.
	 * @param groupId the groupId to check
	 * @return true if the groupId argument is a special group
	 */
	public static boolean isSpecialGroupId(final GroupID groupId) {
		return isSpecialGroupId(groupId.getId());
	}
}