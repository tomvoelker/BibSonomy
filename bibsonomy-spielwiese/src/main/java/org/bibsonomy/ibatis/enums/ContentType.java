package org.bibsonomy.ibatis.enums;

public enum ContentType {
	BOOKMARK(1), BIBTEX(2);

	private final int id;

	private ContentType(final int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}
}