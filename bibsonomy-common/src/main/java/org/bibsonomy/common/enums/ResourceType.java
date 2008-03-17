package org.bibsonomy.common.enums;

/**
 * @author Dominik Benz
 * @version $Id$
 */
public enum ResourceType {
	/** Bookmark */
	BOOKMARK,
	/** BibTex */
	BIBTEX,
	/** All */
	ALL;

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
		return this.name().toLowerCase();
	}
}