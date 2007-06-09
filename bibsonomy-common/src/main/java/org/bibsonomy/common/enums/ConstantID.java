package org.bibsonomy.common.enums;

/**
 * Constants that are used in SQL statements
 *
 * @author Christian Schenk
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

	/* names for ids table */
	IDS_CONTENT_ID(0),
	IDS_TAS_ID(1),
	IDS_TAGREL_ID(2),
	IDS_QUESTION_ID(3),
	IDS_CYCLE_ID(4),
	IDS_EXTENDED_FIELDS(5),
	IDS_SCRAPER_METADATA(7),
	IDS_UNDEFINED_CONTENT_ID(-1),

	/* Spammer ids */
	SPAMMER_TRUE(1),
	SPAMMER_FALSE(0);

	private final int id;

	private ConstantID(final int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}
}