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
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetBookmarksByFriends extends BookmarkChainElement {

	/**
	 * return a list of bookmark entries of your friends in bibSonomy
	 * TODO extension with user restriction rearding returned bookmarks and
	 * appropriate naming of URL in REST interface
	 * 
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
		return present(param.getUserName()) && (param.getGrouping() == GroupingEntity.FRIEND) && present(param.getRequestedGroupName()) && present(param.getRequestedUserName()) && !present(param.getTagIndex()) && !present(param.getHash()) && !present(param.getOrder()) && !present(param.getSearch());
	}
}