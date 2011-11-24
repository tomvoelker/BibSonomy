package org.bibsonomy.common.enums;

/**
 * @author rja
 * @version $Id$
 */
public enum PreviewSize {
	/**
	 * 
	 */
	SMALL("small"),
	/**
	 * 
	 */
	MEDIUM("medium"),
	/**
	 * 
	 */
	LARGE("large");
	
	final String id;
	
	private PreviewSize(final String id) {
		this.id = id;
	}
	
}
