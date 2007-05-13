package org.bibsonomy.database.managers.chain.bookmark.get;

import java.util.List;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bookmark.BookmarkChainElement;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.util.Transaction;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;

/**
 * TODO check
 * 
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetBookmarksOfFriendsByUser extends BookmarkChainElement {

	/**
	 * return a list of bookmark by given friends of a user (this friends also
	 * posted this bookmarks to group friends, made bookmarks viewable for
	 * friends). Following arguments have to be given:
	 * 
	 * at first all bookmarks of user x are returned, sencondly this list is
	 * restricted by those post which are posted to group friend, respectively
	 * are viewable for friends e.g. mgr/friend/stumme
	 * 
	 * bookmarks are listed which record me as friend and also posted this
	 * record to the group friend
	 * 
	 * grouping:friend name:given tags:NULL hash:NULL popular:false added:false
	 * /user/friend
	 */
	@Override
	protected List<Post<Bookmark>> handle(final BookmarkParam param, final Transaction session) {
		log.debug(this.getClass().getSimpleName());
		param.setGroupId(ConstantID.GROUP_FRIENDS.getId());
		return this.db.getBookmarkForUser(param, session);
	}

	@Override
	protected boolean canHandle(final BookmarkParam param) {
		return param.getUserName() != null && param.getGrouping() == GroupingEntity.FRIEND && param.getRequestedGroupName() != null && param.getTagIndex() == null && param.getHash() == null && param.isPopular() == false && param.isAdded() == false;
	}
}