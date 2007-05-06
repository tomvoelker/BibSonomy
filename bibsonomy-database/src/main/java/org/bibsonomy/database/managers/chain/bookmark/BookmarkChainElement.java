package org.bibsonomy.database.managers.chain.bookmark;

import org.bibsonomy.database.managers.BookmarkDatabaseManager;
import org.bibsonomy.database.managers.chain.ChainElement;
import org.bibsonomy.model.Bookmark;

/**
 * All elements for the chain of responsibility for bookmarks are derived from
 * this class.
 * 
 * @author Miranda Grahl
 * @version $Id$
 */
public abstract class BookmarkChainElement extends ChainElement<Bookmark> {

	protected final BookmarkDatabaseManager db;

	public BookmarkChainElement() {
		this.db = BookmarkDatabaseManager.getInstance();
	}
}