package org.bibsonomy.model.enums;

/**
 * @author Jens Illig
 * @version $Id$
 */
public enum ImportFormat {
	/**
	 * MARC format
	 */
	MARC("application/marc"),
	/**
	 * PICA format TODO: correct?
	 */
	PICA("application/pica"),
	/**
	 * Bibtex format TODO: correct?
	 */
	BIBTEX("application/bibtex"),
	/**
	 * Endnote format TODO: correct?
	 */
	ENDNOTE("application/endnote"),
	/**
	 * Temporary support for MARC format with some fields read from PICA
	 */
	MARC_PLUS_PICA("application/marcpica");
	
	private final String mimeType;
	
	private ImportFormat(String mimeType) {
		this.mimeType = mimeType;
	}

	/**
	 * @return the mimeType
	 */
	public String getMimeType() {
		return this.mimeType;
	}
	
	
}
