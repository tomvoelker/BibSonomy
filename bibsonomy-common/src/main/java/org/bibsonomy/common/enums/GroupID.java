package org.bibsonomy.common.enums;


public enum GroupID {
	/* constant group ids */
	PUBLIC(0),
	PRIVATE(1),
	FRIENDS(2),
	KDE(3),
	INVALID(-1);

	private final int id;

	private GroupID(final int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}
	
	public static GroupID getSpecialGroup(final String name) {
		final GroupID group = valueOf( name.toUpperCase() );
		if (isSpecialGroupId(group.id)) {
			return group;
		}
		return null;
	}

	public static boolean isSpecialGroupId(int groupId) {
		return ((groupId < 3) && (groupId >= 0));
	}
}