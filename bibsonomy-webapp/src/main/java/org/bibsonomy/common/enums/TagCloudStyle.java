package org.bibsonomy.common.enums;

/**
 * Presentation modes for tag clouds
 *
 * @version: $Id$
 * @author:  dbenz
 *
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
 	 * @return
 	 */
 	public static TagCloudStyle getStyle(int id) {
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
