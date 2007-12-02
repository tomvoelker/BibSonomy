package org.bibsonomy.common.enums;

/**
 * @author Dominik Benz
 * @version $Id$
 */
public enum ResourceType {
	/** Bookmark */
	BOOKMARK("bookmark"),
	/** BibTex */
	BIBTEX("bibtex"),
	/** All */
	ALL("all");

	private final String label;

	private ResourceType(final String label) {
		this.label = label;
	}

	/**
	 * Returns the label for this resource, i.e.:
	 * 
	 * <pre>
	 *  BOOKMARK - bookmark
	 *  BIBTEX   - bibtex
	 *  ALL      - all
	 * </pre>
	 * 
	 * @return an all lowercase string for this resource
	 */
	public String getLabel() {
		return this.label;
	}
}