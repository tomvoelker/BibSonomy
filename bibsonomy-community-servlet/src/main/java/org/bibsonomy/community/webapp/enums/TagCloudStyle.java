package org.bibsonomy.community.webapp.enums;

/**
 * Presentation modes for tag clouds
 * 
 * @author Dominik Benz
 * @version $Id$
 */
public enum TagCloudStyle {
	/** cloud representation */
	CLOUD(0),
	/** list representation */
	LIST(1);

	private final int id;

	private TagCloudStyle(final int id) {
		this.id = id;
	}

	/**
	 * @return ID of this tag cloud sort mode
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * @param id
	 * @return a TagCloudStyle object for the corresponding type
	 */
	public static TagCloudStyle getStyle(final int id) {
		switch (id) {
		case 0:
			return CLOUD;
		case 1:
			return LIST;
		default:
			throw new RuntimeException("Style " + id + " doesn't exist.");
		}
	}
}