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
		// TODO: is this needed?  param.setGroups(generalDb.getGroupsForUser(param, session));
		return this.db.getBookmarkByHashForUser(param, session);
	}

	@Override
	protected boolean canHandle(final BookmarkParam param) {
		return present(param.getHash()) && (param.getGrouping() == GroupingEntity.USER) && present(param.getRequestedGroupName()) && !present(param.getTagIndex()) && !present(param.getOrder());
	}
}