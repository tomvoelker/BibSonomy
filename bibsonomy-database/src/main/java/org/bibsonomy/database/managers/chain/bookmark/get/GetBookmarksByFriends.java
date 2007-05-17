package org.bibsonomy.database.managers.chain.bookmark.get;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bookmark.BookmarkChainElement;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.util.Transaction;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;

/**
 * TODO check
 * 
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetBookmarksByFriends extends BookmarkChainElement {

	/**
	 * TODO extension with user restriction rearding returned bookmarks and
	 * appropriate naming of URL in REST interface
	 * 
	 * grouping:friend name:given tags:NULL hash:NULL popular:false added:false
	 * /user/friend
	 */
	@Override
	protected List<Post<Bookmark>> handle(final BookmarkParam param, final Transaction session) {
		return this.db.getBookmarkByUserFriends(param, session);
	}

	/*
	 * TODO username: semantik fehlt in API
	 */
	@Override
	protected boolean canHandle(final BookmarkParam param) {
		return param.getUserName() != null && param.getGrouping() == GroupingEntity.FRIEND && param.getRequestedGroupName() != null && param.getTagIndex() == null && param.getHash() == null && param.isPopular() == false && param.isAdded() == false;
	}
}