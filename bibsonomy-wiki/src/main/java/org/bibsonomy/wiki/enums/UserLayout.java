package org.bibsonomy.wiki.enums;

public enum UserLayout {
	LAYOUT_DEFAULT_II_EN("cv.layout.robert.en"),
	LAYOUT_DEFAULT_II_GER("cv.layout.robert.ger"),
	LAYOUT_DEFAULT_I_EN("cv.layout.table.en"),
	LAYOUT_DEFAULT_I_GER("cv.layout.table.ger"),
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
