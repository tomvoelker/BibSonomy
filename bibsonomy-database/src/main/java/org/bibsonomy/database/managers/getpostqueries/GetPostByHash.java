package org.bibsonomy.database.managers.getpostqueries;

import java.util.List;

import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

public class GetPostByHash extends RequestHandlerForGetPost {

	protected boolean canHandle(String authUser, String resourceHash, String currUser) {
		if (resourceHash != null && resourceHash.length() > 0) {
			return true;
		}
		return false;
	}

	@Override
	protected Post<? extends Resource> handleRequestForGetPost(String authUser, String resourceHash, String currUser) {

		final BookmarkParam param = new BookmarkParam();
		param.setRequestedUserName(authUser);
		param.setHash(resourceHash);
		param.setUserName(currUser);
		List<Post<? extends Resource>> posts = db.bookmarkDatabaseManager.bookmarkList("getBookmarkByHash", param, true);
		if( posts.size() == 1 )
		{
			return posts.get( 0 );
		}
		
		else if( posts.size() > 1 )
		{
			// TODO log
		}
		return null;
	}
}
