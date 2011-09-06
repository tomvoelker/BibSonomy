package org.bibsonomy.wiki.enums;

public enum UserLayout {
	LAYOUT_ROBERT("cv.layout.robert"),
	LAYOUT_TABLE("cv.layout.table"),
	LAYOUT_CURRENT("");
	
	private final String ref;
	private UserLayout(final String ref) {
		this.ref = ref;
	}

	/**
	 * @return the ref
	 */
	public String getRef() {
		return this.ref;
	}
}
