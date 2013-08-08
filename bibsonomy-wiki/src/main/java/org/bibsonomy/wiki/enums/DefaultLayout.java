package org.bibsonomy.wiki.enums;

/**
 * Enum to summarize the default layouts (i.e. wiki texts) available for the CV
 * wiki. These map to files in the wiki submodule. the encoded strings represent
 * the file prefix for a layout.
 * 
 * @author Dominik Benz, benz@cs.uni-kassel.de
 * @author Bernd Terbrack
 * @author Thomas Niebler
 */
public enum DefaultLayout {

	/** default user layout 2 (english) */
	LAYOUT_DEFAULT_II_EN("user2en"),

	/** default user layout 2 (german) */
	LAYOUT_DEFAULT_II_GER("user2de"),

	/** default user layout 1 (english) */
	LAYOUT_DEFAULT_I_EN("user1en"),

	/** default user layout 2 (german) */
	LAYOUT_DEFAULT_I_GER("user1de"),

	/** current user layout (i.e. the one ) */
	LAYOUT_CURRENT(""),

	/** default group layout 1 (english) */
	LAYOUT_G_DEFAULT_I_EN("group1en"),

	/** default group layout 1 (german) */
	LAYOUT_G_DEFAULT_I_GER("group1de");

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
