package org.bibsonomy.database.managers.chain.bookmark.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.bookmark.BookmarkChainElement;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;

/**
 * Returns a list of bookmarks of users you are following.
 * 
 * @author Dominik Benz
 * @version $Id$
 */
public class GetBookmarksByFollowedUsers extends BookmarkChainElement {

	@Override
	protected List<Post<Bookmark>> handle(final BookmarkParam param, final DBSession session) {
		return this.db.getPostsByFollowedUsers(param.getUserName(), param.getGroups(), param.getLimit(), param.getOffset(), session);
	}

	@Override
	protected boolean canHandle(final BookmarkParam param) {
		return (present(param.getUserName()) &&
				present(param.getGroups()) &&
				param.getGrouping() == GroupingEntity.FOLLOWER);
	}
}