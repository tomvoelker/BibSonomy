
package org.bibsonomy.database.managers.getpostsqueriesForBookmark;

import org.bibsonomy.database.managers.BookmarkDatabaseManager;
import org.bibsonomy.database.managers.RequestHandlerForGetPosts;

/*******
* 
* @author mgr
*
**/

public abstract class RequestHandlerForGetBookmarkPosts extends RequestHandlerForGetPosts {
	protected final BookmarkDatabaseManager db = BookmarkDatabaseManager.getInstance();
}