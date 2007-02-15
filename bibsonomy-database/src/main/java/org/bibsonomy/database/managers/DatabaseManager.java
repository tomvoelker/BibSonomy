package org.bibsonomy.database.managers;



/**
 * This singleton class supplies all database-managers which can execute queries
 * on the database.
 * 
 * @author Christian Schenk
 */
public final class DatabaseManager {

	
	/** The singleton */
	public static final DatabaseManager singleton = new DatabaseManager();
	public final GeneralDatabaseManager generalDatabaseManager;
	public final BookmarkDatabaseManager bookmarkDatabaseManager;
	public final BibTexDatabaseManager bibtexDatabaseManager;
	public final TagDatabaseManager tagDatabaseManager;

	/**
	 * The constructor is private due to the singleton pattern.
	 */
	public DatabaseManager() {
		this.generalDatabaseManager = new GeneralDatabaseManager();
		this.bookmarkDatabaseManager = new BookmarkDatabaseManager(this);
		this.bibtexDatabaseManager = new BibTexDatabaseManager(this);
		this.tagDatabaseManager = new TagDatabaseManager();
	}

	/**
	 * Returns an instance of this class.
	 */
	public static final DatabaseManager getInstance() {
		return singleton;
	}

	/**
	 * Sets all database managers to readonly, i.e. the database won't be
	 * changed by inserts and the like.
	 */
	public final void setReadonly() {
		this.getGeneral().setReadonly(true);
		this.getBookmark().setReadonly(true);
		this.getBibTex().setReadonly(true);
		this.getTag().setReadonly(true);
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