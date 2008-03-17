package org.bibsonomy.common.enums;

/**
 * Constants that are used in SQL statements
 * 
 * @author Christian Schenk
 * @author Christian Kramer
 * @version $Id$
 */
public enum ConstantID {
	/*
	 * SQL constants
	 */
	/** Contenttype for Bookmark */
	BOOKMARK_CONTENT_TYPE(1),
	/** Contenttype for BibTeX */
	BIBTEX_CONTENT_TYPE(2),
	/** Contenttype for ALL contents */
	ALL_CONTENT_TYPE(0),

	/* names for ids table */
	/** id of the contentId in ids table */
	IDS_CONTENT_ID(0),
	/** id of the tasId in ids table */
	IDS_TAS_ID(1),
	/** id of the tagRelId in ids table */
	IDS_TAGREL_ID(2),
	/** id of the quastionId in ids table */
	IDS_QUESTION_ID(3),
	/** id of the cycleId in ids table */
	IDS_CYCLE_ID(4),
	/** id of the exgtendedFieldsId in ids table */
	IDS_EXTENDED_FIELDS(5),
	/** id of the scraperMetadataId in ids table */
	IDS_SCRAPER_METADATA(7),

	/* other ids (not related to SQL tables! */
	/** marks that no special content type has yet been assigned */
	IDS_UNDEFINED_CONTENT_ID(-1);

	private final int id;

	private ConstantID(final int id) {
		this.id = id;
	}

	/**
	 * @return the id constant behind the symbol
	 */
	public int getId() {
		return this.id;
	}
}