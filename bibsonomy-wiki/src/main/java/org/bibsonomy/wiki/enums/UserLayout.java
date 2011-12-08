package org.bibsonomy.wiki.enums;

/**
 * TODO: merge with GroupLayout and rename
 * 
 * @author Bernd
 * @version $Id$
 */
public enum UserLayout {
	/**
	 * default user layout 2 (english)
	 */
	LAYOUT_DEFAULT_II_EN("cv.layout.user.def2en"),
	
	/**
	 * default user layout 2 (german)
	 */
	LAYOUT_DEFAULT_II_GER("cv.layout.user.def2ger"),
	
	/**
	 * default user layout (english)
	 */
	LAYOUT_DEFAULT_I_EN("cv.layout.user.def1en"),
	
	/**
	 * default user layout (german)
	 */
	LAYOUT_DEFAULT_I_GER("cv.layout.user.def1ger"),
	
	/**
	 * current user layout
	 */
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
