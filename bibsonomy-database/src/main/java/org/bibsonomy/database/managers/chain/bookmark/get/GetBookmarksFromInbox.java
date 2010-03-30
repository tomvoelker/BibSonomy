package org.bibsonomy.database.managers.chain.bookmark.get;

import static org.bibsonomy.util.ValidationUtils.present;

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
		if (present(param.getHash())) {
			/*
			 * If an intraHash is given, we retrieve only the posts with this hash from the users inbox 
			 */
			return this.db.getPostsFromInboxByHash(param.getUserName(), param.getHash(), session);
		}
		/*
		 * return all posts from the users inbox
		 */
		return this.db.getPostsFromInbox(param.getUserName(), param.getLimit(), param.getOffset(), session);
	}

	@Override
	protected boolean canHandle(BookmarkParam param) {
		return (param.getGrouping() == GroupingEntity.INBOX);
	}
}
