package org.bibsonomy.database.managers.chain.bookmark.get;

import static org.bibsonomy.util.ValidationUtils.nullOrEqual;
import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.bookmark.BookmarkChainElement;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.enums.Order;

/**
 * Returns a list of bookmarks of your friends.
 * 
 * TODO extension with user restriction rearding returned bookmarks and
 * appropriate naming of URL in REST interface.
 * 
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetBookmarksByFriends extends BookmarkChainElement {

	@Override
	protected List<Post<Bookmark>> handle(final BookmarkParam param, final DBSession session) {
		return this.db.getPostsByUserFriends(param.getUserName(), HashID.getSimHash(param.getSimHash()), param.getLimit(), param.getOffset(), param.getSystemTags().values(), session);
	}

	/*
	 * TODO username: semantik fehlt in API
	 */
	@Override
	protected boolean canHandle(final BookmarkParam param) {
		return (present(param.getUserName()) &&
				param.getGrouping() == GroupingEntity.FRIEND &&
				!present(param.getRequestedGroupName()) &&
				!present(param.getRequestedUserName()) &&
				!present(param.getTagIndex()) &&
				!present(param.getHash()) &&
				nullOrEqual(param.getOrder(), Order.ADDED) &&
				!present(param.getSearch()));
	}
}