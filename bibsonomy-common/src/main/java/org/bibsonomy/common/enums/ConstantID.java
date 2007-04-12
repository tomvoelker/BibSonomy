package org.bibsonomy.common.enums;

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
	GROUP_INVALID(-1),
	GROUP_PUBLIC(0),
	GROUP_PRIVATE(1),
	GROUP_FRIENDS(2),
	GROUP_KDE(3),

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

	/* Constant for SimHash */
	SIM_HASH0(0),
	SIM_HASH1(1),
	SIM_HASH2(2),
	SIM_HASH3(3),
	SIM_HASH(SIM_HASH1),
	INTRA_HASH(SIM_HASH0),

	/* Spammer ids */
	SPAMMER_TRUE(1),
	SPAMMER_FALSE(0);

	private final int id;

	private ConstantID(final int id) {
		this.id = id;
	}

	private ConstantID(final ConstantID id) {
		this.id = id.getId();
	}

	public int getId() {
		return this.id;
	}

	/**
	 * Returns the corresponding simhash.
	 */
	public static ConstantID getSimHash(final int simHash) {
		switch (simHash) {
		case 0:
			return SIM_HASH0;
		case 1:
			return SIM_HASH1;
		case 2:
			return SIM_HASH2;
		case 3:
			return SIM_HASH3;
		default:
			throw new RuntimeException("SimHash " + simHash + " doesn't exist.");
		}
	}
}