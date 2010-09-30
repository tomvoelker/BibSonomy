package org.bibsonomy.database.util;

import org.bibsonomy.database.managers.BibTexDatabaseManager;
import org.bibsonomy.database.managers.BookmarkDatabaseManager;
import org.bibsonomy.database.managers.GoldStandardPublicationDatabaseManager;
import org.bibsonomy.database.managers.GroupDatabaseManager;
import org.bibsonomy.database.managers.TagDatabaseManager;
import org.bibsonomy.database.managers.UserDatabaseManager;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.services.searcher.ResourceSearch;

/**
 * class for configuring some properties in all relevant database managers as 
 * configured via spring 
 * 
 * due to its simplicity, this class is not refactored 
 * 
 * @author fei
 * @version $Id$
 */
public class DatabaseManagerInitializer {

	/** the bibtex resource searcher */
	private ResourceSearch<BibTex> bibTexSearcher;

	/** the bookmark resource searcher */
	private ResourceSearch<Bookmark> bookmarkSearcher;
	
	/** the publication database manager */
	private final BibTexDatabaseManager bibTexManager;
	
	/** the bookmark database manager */
	private final BookmarkDatabaseManager bookmarkManager;

	/** the tag database manager */
	private final TagDatabaseManager tagManager;

	private final GoldStandardPublicationDatabaseManager goldStandardPublicationManager;

	/**
	 * inits the user and group db manager
	 */
	public DatabaseManagerInitializer() {
		// FIXME: we have to initialize the db managers in a given order 
		//        to prevent circular dependencies!!!
		//        Better use spring for configuring the database module
		UserDatabaseManager userDbManager = UserDatabaseManager.getInstance();
		GroupDatabaseManager groupDbManager = GroupDatabaseManager.getInstance();
		groupDbManager.setUserDb(userDbManager);
		
		this.tagManager      = TagDatabaseManager.getInstance();
		this.bibTexManager   = BibTexDatabaseManager.getInstance();
		this.bookmarkManager = BookmarkDatabaseManager.getInstance();
		this.goldStandardPublicationManager = GoldStandardPublicationDatabaseManager.getInstance();
	}
	
	/**
	 * also sets the publication searcher in the publication and tag manager
	 * 
	 * @param bibTexSearcher the bibTexSearcher to set
	 */
	public void setBibTexSearcher(ResourceSearch<BibTex> bibTexSearcher) {
		this.bibTexSearcher = bibTexSearcher;
		this.bibTexManager.setResourceSearch(this.bibTexSearcher);
		this.tagManager.setPublicationSearch(bibTexSearcher);
	}

	/**
	 * @return the bibTexSearcher
	 */
	public ResourceSearch<BibTex> getBibTexSearcher() {
		return bibTexSearcher;
	}

	/**
	 * also sets the bookmark searcher in the bookmark manager
	 * 
	 * @param bookmarkSearcher the bookmarkSearcher to set
	 */
	public void setBookmarkSearcher(ResourceSearch<Bookmark> bookmarkSearcher) {
		this.bookmarkSearcher = bookmarkSearcher;
		this.bookmarkManager.setResourceSearch(bookmarkSearcher);
		this.tagManager.setBookmarkSearch(bookmarkSearcher);
	}

	/**
	 * @return the bookmarkSearcher
	 */
	public ResourceSearch<Bookmark> getBookmarkSearcher() {
		return bookmarkSearcher;
	}

	/**
	 * sets the searcher for the goldStandardPublication manager
	 * @param goldStandardPublicationSearcher
	 */
	public void setGoldStandardPublicationSearcher(final ResourceSearch<GoldStandardPublication> goldStandardPublicationSearcher) {
	    this.goldStandardPublicationManager.setSearcher(goldStandardPublicationSearcher);
	}
}
