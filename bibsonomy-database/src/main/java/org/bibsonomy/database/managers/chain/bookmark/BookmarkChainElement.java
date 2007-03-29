package org.bibsonomy.database.managers.chain.bookmark;

import org.bibsonomy.database.managers.BookmarkDatabaseManager;
import org.bibsonomy.database.managers.chain.ChainElement;

/**
 * @author mgr
 */
public abstract class BookmarkChainElement extends ChainElement {
	protected final BookmarkDatabaseManager db = BookmarkDatabaseManager.getInstance();
}