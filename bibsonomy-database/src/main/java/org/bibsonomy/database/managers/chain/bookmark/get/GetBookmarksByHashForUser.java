package org.bibsonomy.database.managers.chain.bookmark.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.database.managers.chain.bookmark.BookmarkChainElement;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;

/**
 * Returns a list of bookmarks for a given hash and a user.
 * 
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetBookmarksByHashForUser extends BookmarkChainElement {

	@Override
	protected List<Post<Bookmark>> handle(final BookmarkParam param, final DBSession session) {
		return this.db.getPostsByHashForUser(param.getUserName(), param.getHash(), param.getRequestedUserName(), param.getGroups(), HashID.getSimHash(param.getSimHash()), session);
	}

	@Override
	protected boolean canHandle(final BookmarkParam param) {
		return (present(param.getHash()) &&
				param.getGrouping() == GroupingEntity.USER &&
				present(param.getRequestedGroupName()) &&
				!present(param.getTagIndex()) &&
				!present(param.getOrder()) &&
				!present(param.getSearch()));
	}
}