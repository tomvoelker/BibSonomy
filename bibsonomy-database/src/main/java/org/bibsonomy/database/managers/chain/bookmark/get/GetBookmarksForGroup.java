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
public class GetBookmarksForGroup extends BookmarkChainElement {

	/**
	 * return a list of bookmark by a given group. Following arguments have to
	 * be given:
	 * 
	 * grouping:group name:given tags:NULL hash:null popular:false added:false
	 */
	@Override
	protected List<Post<Bookmark>> handle(final BookmarkParam param, final Transaction session) {
		log.debug(this.getClass().getSimpleName());

		param.setGroupId(this.generalDb.getGroupIdByGroupName(param, session));
		param.setGroups(this.generalDb.getGroupsForUser(param, session));

		return this.db.getBookmarkForGroup(param, session);
	}

	@Override
	protected boolean canHandle(final BookmarkParam param) {
		return param.getUserName() != null && param.getGrouping() == GroupingEntity.GROUP && param.getRequestedGroupName() != null && param.getTagIndex() == null && param.getHash() == null && param.isPopular() == false && param.isAdded() == false;
	}
}