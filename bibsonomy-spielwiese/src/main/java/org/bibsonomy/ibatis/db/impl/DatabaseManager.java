package org.bibsonomy.ibatis.db.impl;

public final class DatabaseManager {

	private static final DatabaseManager singleton = new DatabaseManager();
	private final GeneralDatabaseManager generalDatabaseManager;
	private final BookmarkDatabaseManager bookmarkDatabaseManager;
	private final BibTexDatabaseManager bibtexDatabaseManager;
	private final TagDatabaseManager tagDatabaseManager;

	private DatabaseManager() {
		this.generalDatabaseManager = new GeneralDatabaseManager();
		this.bookmarkDatabaseManager = new BookmarkDatabaseManager();
		this.bibtexDatabaseManager = new BibTexDatabaseManager();
		this.tagDatabaseManager = new TagDatabaseManager();
	}

	public static final DatabaseManager getInstance() {
		return singleton;
	}

	public final GeneralDatabaseManager getGeneral() {
		return this.generalDatabaseManager;
	}

	public final BookmarkDatabaseManager getBookmark() {
		return this.bookmarkDatabaseManager;
	}

	public final BibTexDatabaseManager getBibTex() {
		return this.bibtexDatabaseManager;
	}

	public TagDatabaseManager getTag() {
		return this.tagDatabaseManager;
	}	
}