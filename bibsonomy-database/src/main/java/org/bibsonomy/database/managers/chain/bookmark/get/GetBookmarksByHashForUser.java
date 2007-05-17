package org.bibsonomy.database.managers.chain.bookmark.get;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bookmark.BookmarkChainElement;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.util.Transaction;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;

/**
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetBookmarksByHashForUser extends BookmarkChainElement {

	/**
	 * return a list of bookmark by a given hash and a logged user. Following
	 * arguments have to be given:
	 * 
	 * grouping:user name:given tags:NULL hash:given popular:false added:false
	 */
	@Override
	protected List<Post<Bookmark>> handle(final BookmarkParam param, final Transaction session) {
		param.setGroups(generalDb.getGroupsForUser(param, session));
		return this.db.getBookmarkByHashForUser(param, session);
	}

	@Override
	protected boolean canHandle(final BookmarkParam param) {
		return param.getHash() != null && param.getHash().length() > 0 && param.getUserName() != null && param.getGrouping() == GroupingEntity.USER && param.getRequestedGroupName() != null && param.getTagIndex() == null && param.isPopular() == false && param.isAdded() == false;
	}
}