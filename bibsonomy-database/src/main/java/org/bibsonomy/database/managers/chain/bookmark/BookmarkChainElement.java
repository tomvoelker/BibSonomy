package org.bibsonomy.database.managers.chain.bookmark;

import org.bibsonomy.database.managers.BookmarkDatabaseManager;
import org.bibsonomy.database.managers.chain.ListChainElement;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;

/**
 * All elements for the chain of responsibility for bookmarks are derived from
 * this class.
 * 
 * @author Miranda Grahl
 * @version $Id$
 */
public abstract class BookmarkChainElement extends ListChainElement<Post<Bookmark>, BookmarkParam> {

	protected final BookmarkDatabaseManager db;

	/**
	 * Constructs a chain element
	 */
	public BookmarkChainElement() {
		this.db = BookmarkDatabaseManager.getInstance();
	}
}