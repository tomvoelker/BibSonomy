package org.bibsonomy.database.managers.chain.bookmark.get;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bookmark.BookmarkChainElement;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;

/**
 * 
 * Returns the posts in the users inbox
 * @author sdo
 * @version $Id$
 */
public class GetBookmarksFromInbox extends BookmarkChainElement {
	
	@Override
	protected List<Post<Bookmark>> handle(BookmarkParam param, DBSession session) {
		return this.db.getPostsFromInbox(param, session);
	}

	@Override
	protected boolean canHandle(BookmarkParam param) {
		return (param.getGrouping() == GroupingEntity.INBOX);
	}
}
