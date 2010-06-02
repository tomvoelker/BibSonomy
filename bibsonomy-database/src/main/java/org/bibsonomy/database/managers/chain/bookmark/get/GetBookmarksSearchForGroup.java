package org.bibsonomy.database.managers.chain.bookmark.get;

import java.util.List;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.bookmark.BookmarkChainElement;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;

/**
 * Returns a list of bookmarks for a given search.
 * @author claus
 * @version $Id$
 */
public class GetBookmarksSearchForGroup extends BookmarkChainElement {

	@Override
	protected List<Post<Bookmark>> handle(final BookmarkParam param, final DBSession session) {
		
		return this.db.getPostsSearchForGroup(param.getRequestedGroupName(), param.getGroupNames(), param.getRawSearch(), param.getUserName(), param.getLimit(), param.getOffset(), param.getSystemTags().values(), session);
	}
	
	@Override
	protected boolean canHandle(final BookmarkParam param) {
		return false;
	}

}
