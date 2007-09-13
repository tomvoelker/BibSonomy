package org.bibsonomy.database.managers.chain.bookmark.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bookmark.BookmarkChainElement;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;

/**
 * 
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetBookmarksOfFriendsByTags extends BookmarkChainElement {

	/**
	 * return a list of bookmark entries by given friend of a user (this friends also
	 * posted this bookmarks to group friends (made bookmarks viewable for
	 * friends)).
	 * bookmark entries are restricted by a chosen tag 
	 */
	@Override
	protected List<Post<Bookmark>> handle(final BookmarkParam param, final DBSession session) {
		param.setGroupId(GroupID.FRIENDS.getId());
		return this.db.getBookmarkForUser(param, session);
	}

	@Override
	protected boolean canHandle(final BookmarkParam param) {
		return present(param.getUserName()) && (param.getGrouping() == GroupingEntity.FRIEND) && present(param.getRequestedGroupName())&& !present(param.getRequestedUserName()) && present(param.getTagIndex()) && !present(param.getHash()) && !present(param.getOrder());
	}
}