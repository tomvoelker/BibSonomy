package org.bibsonomy.wiki.enums;

/**
 * Enum to summarize the default layouts (i.e. wiki texts) available for the CV
 * wiki. These map to message keys defined in messages*.properties (there, the
 * wiki texts are stored)
 * 
 * @author Dominik Benz, benz@cs.uni-kassel.de
 * @author Bernd Terbrack
 */
public enum DefaultLayout {

	/** default user layout 2 (english) */
	LAYOUT_DEFAULT_II_EN("cv.layout.user.def2en"),

	/** default user layout 2 (german) */
	LAYOUT_DEFAULT_II_GER("cv.layout.user.def2ger"),

	/** default user layout 1 (english) */
	LAYOUT_DEFAULT_I_EN("cv.layout.user.def1en"),

	/** default user layout 2 (german) */
	LAYOUT_DEFAULT_I_GER("cv.layout.user.def1ger"),

	/** current user layout (i.e. the one ) */
	LAYOUT_CURRENT(""),

	/** default group layout 1 (english) */
	LAYOUT_G_DEFAULT_I_EN("cv.layout.group.def1en"),

	/** default group layout 1 (german) */
	LAYOUT_G_DEFAULT_I_GER("cv.layout.group.def1ger");

	/** the message key which holds the default cv wiki text */
	private final String ref;

	/**
	 * Constructor
	 * 
	 * @param ref
	 *            - the message key
	 */
	private DefaultLayout(final String ref) {
		this.ref = ref;
	}

	/**
	 * Get the message key for the current default layout
	 * 
	 * @return the message key for the given default cv wiki
	 */
	public String getRef() {
		return this.ref;
	}
}
