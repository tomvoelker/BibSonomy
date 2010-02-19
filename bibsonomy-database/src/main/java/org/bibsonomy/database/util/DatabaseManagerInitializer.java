package org.bibsonomy.database.util;

import org.bibsonomy.database.managers.BibTexDatabaseManager;
import org.bibsonomy.database.managers.BookmarkDatabaseManager;
import org.bibsonomy.database.managers.GroupDatabaseManager;
import org.bibsonomy.database.managers.TagDatabaseManager;
import org.bibsonomy.database.managers.UserDatabaseManager;
import org.bibsonomy.database.systemstags.SystemTagFactory;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
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
	
	/** the system tag factory */
	private SystemTagFactory systemTagFactory;
	
	/** the publication database manager */
	private final BibTexDatabaseManager bibTexManager;
	
	/** the bookmark database manager */
	private final BookmarkDatabaseManager bookmarkManager;

	/** the tag database manager */
	private final TagDatabaseManager tagManager;

	
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
	}
	
	//------------------------------------------------------------------------
	// getter/setter
	//------------------------------------------------------------------------
	public void setBibTexSearcher(ResourceSearch<BibTex> bibTexSearcher) {
		this.bibTexSearcher = bibTexSearcher;
		this.bibTexManager.setResourceSearch(this.bibTexSearcher);
		this.tagManager.setAuthorSearch(bibTexSearcher);
	}

	public ResourceSearch<BibTex> getBibTexSearcher() {
		return bibTexSearcher;
	}

	public void setBookmarkSearcher(ResourceSearch<Bookmark> bookmarkSearcher) {
		this.bookmarkSearcher = bookmarkSearcher;
		this.bookmarkManager.setResourceSearch(bookmarkSearcher);
	}

	public ResourceSearch<Bookmark> getBookmarkSearcher() {
		return bookmarkSearcher;
	}

	public void setSystemTagFactory(SystemTagFactory systemTagFactory) {
		this.systemTagFactory = systemTagFactory;
		this.bibTexManager.setSystemTagFactory(systemTagFactory);
		this.bookmarkManager.setSystemTagFactory(systemTagFactory);
	}

	public SystemTagFactory getSystemTagFactory() {
		return systemTagFactory;
	}
	
	
}
