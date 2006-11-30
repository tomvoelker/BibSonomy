package org.bibsonomy.ibatis.enums;

/**
 * Constants that are used in SQL statements
 *
 * @author Christian Schenk
 */
public enum ConstantID {
	/*
	 * SQL constants
	 */
	/** Contenttype for Bookmark */
	BOOKMARK_CONTENT_TYPE(1),
	/** Contenttype for BibTeX */
	BIBTEX_CONTENT_TYPE(2),

	/* constant group ids */
	GROUP_PUBLIC(0),
	GROUP_PRIVATE(1),
	GROUP_FRIENDS(2),

	/* privacy levels for groups */
	/** member list public */
	PRIVLEVEL_PUBLIC(0),
	/** member list hidden */
	PRIVLEVEL_HIDDEN(1),
	/** members can list members */
	PRIVLEVEL_MEMBERS(2),

	/* names for ids table */
	IDS_CONTENT_ID(0),
	IDS_TAS_ID(1),
	IDS_TAGREL_ID(2),
	IDS_QUESTION_ID(3),
	IDS_CYCLE_ID(4),
	IDS_EXTENDED_FIELDS(5),
	IDS_SCRAPER_METADATA(7),
	IDS_UNDEFINED_CONTENT_ID(-1),
     
	/*
	 * constant for simhash
	 */
	
	SIM_HASH(1),
    
	
	/*spammer ids*/
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