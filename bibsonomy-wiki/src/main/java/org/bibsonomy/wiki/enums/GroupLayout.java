package org.bibsonomy.wiki.enums;

public enum GroupLayout {
	LAYOUT_G_DEFAULT_I_EN("cv.layout.group.def1en"),
	LAYOUT_G_DEFAULT_I_GER("cv.layout.group.def1ger"),
	LAYOUT_CURRENT("");
	
	private final String ref;
	private GroupLayout(final String ref) {
		this.ref = ref;
	}

	/**
	 * @return the ref
	 */
	public String getRef() {
		return this.ref;
	}
}
