package org.bibsonomy.model.enums;

/**
 * representation for BibTeX / EndNote
 * 
 * @author dzo
 */
public enum SimpleExportLayout {
	/** endnote */
	ENDNOTE("EndNote"),
	/** BibTeX */
	BIBTEX("BibTeX");
	
	private final String displayName;

	/**
	 * @param displayName
	 */
	private SimpleExportLayout(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return this.displayName;
	}
}
