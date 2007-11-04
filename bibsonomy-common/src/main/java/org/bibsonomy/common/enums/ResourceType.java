package org.bibsonomy.common.enums;

/**
 * @author Dominik Benz
 * @version $Id$
 */
public enum ResourceType {

	BOOKMARK("bookmark"),
	BIBTEX("bibtex"),
	ALL("all");
	
	private final String label;
	
	private ResourceType(final String label) {
		this.label = label;
	}	
	
	public String getLabel() {
		return this.label;
	}	

}