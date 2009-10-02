package org.bibsonomy.database.managers.chain.bookmark.get;

import static org.bibsonomy.util.ValidationUtils.nullOrEqual;
import static org.bibsonomy.util.ValidationUtils.present;
import static org.bibsonomy.util.ValidationUtils.presentValidGroupId;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bookmark.BookmarkChainElement;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.enums.Order;

/**
 * Return a list of bookmarks for a user.
 * 
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetBookmarksForUser extends BookmarkChainElement {

	@Override
	protected List<Post<Bookmark>> handle(final BookmarkParam param, final DBSession session) {
		return this.db.getPostsForUser(param, session);
	}

	@Override
	protected boolean canHandle(final BookmarkParam param) {
		return (param.getGrouping() == GroupingEntity.USER &&
				present(param.getRequestedUserName()) &&
				!presentValidGroupId(param.getGroupId()) &&
				!present(param.getTagIndex()) &&
				!present(param.getHash()) &&
				nullOrEqual(param.getOrder(), Order.ADDED) &&
				!present(param.getSearch()));
	}
}