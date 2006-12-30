package org.bibsonomy.ibatis.db.impl;

/**
 * This singleton class supplies all database-managers which can execute queries
 * on the database.
 * 
 * @author Christian Schenk
 */
public final class DatabaseManager {

	/** The singleton */
	private static final DatabaseManager singleton = new DatabaseManager();
	private final GeneralDatabaseManager generalDatabaseManager;
	private final BookmarkDatabaseManager bookmarkDatabaseManager;
	private final BibTexDatabaseManager bibtexDatabaseManager;
	private final TagDatabaseManager tagDatabaseManager;

	/**
	 * The constructor is private due to the singleton pattern.
	 */
	private DatabaseManager() {
		this.generalDatabaseManager = new GeneralDatabaseManager();
		this.bookmarkDatabaseManager = new BookmarkDatabaseManager();
		this.bibtexDatabaseManager = new BibTexDatabaseManager();
		this.tagDatabaseManager = new TagDatabaseManager();
	}

	/**
	 * Returns an instance of this class.
	 */
	public static final DatabaseManager getInstance() {
		return singleton;
	}

	/**
	 * For general queries.
	 */
	public final GeneralDatabaseManager getGeneral() {
		return this.generalDatabaseManager;
	}

	/**
	 * For queries concerning bookmarks.
	 */
	public final BookmarkDatabaseManager getBookmark() {
		return this.bookmarkDatabaseManager;
	}

	/**
	 * For queries concerning BibTexs.
	 */
	public final BibTexDatabaseManager getBibTex() {
		return this.bibtexDatabaseManager;
	}

	/**
	 * For queries concerning tags.
	 */
	public TagDatabaseManager getTag() {
		return this.tagDatabaseManager;
	}
}