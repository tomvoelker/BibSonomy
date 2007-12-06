package org.bibsonomy.common.enums;

/**
 * sorting modes for tag clouds
 *
 * @version: $Id$
 * @author:  dbenz
 *
 */
public enum TagCloudSort {
	/** alphanumerical sorting */
	ALPHA(0),
	/** sorting by tag frequency */
	FREQ(1);
	
	private final int id;
	
	private TagCloudSort(final int id) {
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
 	public static TagCloudSort getSort(int id) {
 		switch (id) {
 		case 0:
 			return ALPHA;
 		case 1:
 			return FREQ;
		default:
			throw new RuntimeException("Sort " + id + " doesn't exist.");
 		}
 	}	
}
