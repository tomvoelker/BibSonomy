package org.bibsonomy.database.managers.chain.bookmark;

import org.bibsonomy.database.managers.BookmarkDatabaseManager;
import org.bibsonomy.database.managers.chain.ChainElement;

/**
 * All elements for the chain of responsibility for bookmarks are derived from
 * this class.
 * 
 * @author mgr
 */
public abstract class BookmarkChainElement extends ChainElement {

	protected final BookmarkDatabaseManager db;

	public BookmarkChainElement() {
		this.db = BookmarkDatabaseManager.getInstance();
	}
}