package org.bibsonomy.database.managers.chain.bookmark.get;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.Order;
import org.bibsonomy.database.managers.chain.bookmark.BookmarkChainElement;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.util.Transaction;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;

/**
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetBookmarksViewable extends BookmarkChainElement {

	/**
	 * return a list of bookmark by a given group (which is only viewable for
	 * groupmembers excluded public option regarding setting a post). Following
	 * arguments have to be given:
	 * 
	 * grouping:viewable name:given tags:NULL hash:NULL popular:falses
	 * added:false
	 */
	@Override
	protected List<Post<Bookmark>> handle(final BookmarkParam param, final Transaction session) {
		param.setGroupId(this.generalDb.getGroupIdByGroupNameAndUserName(param, session));
		// TODO: is this needed?  param.setGroups(this.generalDb.getGroupsForUser(param, session));

		return this.db.getBookmarkViewable(param, session);
	}

	@Override
	protected boolean canHandle(final BookmarkParam param) {
		return present(param.getUserName()) && (param.getGrouping() == GroupingEntity.VIEWABLE) && present(param.getRequestedGroupName()) && !present(param.getTagIndex()) && !present(param.getHash()) && nullOrEqual(param.getOrder(), Order.ADDED);
	}
}