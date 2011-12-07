package org.bibsonomy.wiki.enums;

public enum UserLayout {
	LAYOUT_DEFAULT_II_EN("cv.layout.user.def2en"),
	LAYOUT_DEFAULT_II_GER("cv.layout.user.def2ger"),
	LAYOUT_DEFAULT_I_EN("cv.layout.user.def1en"),
	LAYOUT_DEFAULT_I_GER("cv.layout.user.def1ger"),
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
