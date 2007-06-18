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
}