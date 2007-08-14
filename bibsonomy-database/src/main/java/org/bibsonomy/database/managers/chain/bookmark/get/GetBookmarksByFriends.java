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
 * TODO check... what?
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
	protected List<Post<Bookmark>> handle(final BookmarkParam param, final DBSession session) {
		return this.db.getBookmarkByUserFriends(param, session);
	}

	/*
	 * TODO username: semantik fehlt in API
	 */
	@Override
	protected boolean canHandle(final BookmarkParam param) {
		return present(param.getUserName()) && (param.getGrouping() == GroupingEntity.FRIEND) && present(param.getRequestedGroupName()) && !present(param.getTagIndex()) && !present(param.getHash()) && !present(param.getOrder());
	}
}