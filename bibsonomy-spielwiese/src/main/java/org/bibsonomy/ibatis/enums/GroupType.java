package org.bibsonomy.ibatis.enums;

public enum GroupType {
	PUBLIC(0), PRIVATE(1), FRIENDS(2);

	private final int id;

	private GroupType(final int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}
}