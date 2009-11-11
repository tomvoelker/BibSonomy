package org.bibsonomy.database.managers.chain.bookmark.get;

import static org.bibsonomy.util.ValidationUtils.nullOrEqual;
import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.database.managers.chain.bookmark.BookmarkChainElement;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.enums.Order;

/**
 * Returns a list of bookmarks for a given group (which is only viewable for
 * groupmembers excluded public option regarding setting a post).
 * 
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetBookmarksViewable extends BookmarkChainElement {

	@Override
	protected List<Post<Bookmark>> handle(final BookmarkParam param, final DBSession session) {
		final Integer groupId = this.groupDb.getGroupIdByGroupNameAndUserName(param.getRequestedGroupName(), param.getUserName(), session);
		if (groupId == GroupID.INVALID.getId()) {
			log.debug("groupId " + param.getRequestedGroupName() + "not found");
			return new ArrayList<Post<Bookmark>>(0);
		}
		
		if (present(param.getTagIndex())) {
			return this.db.getPostsViewableByTag(param.getRequestedUserName(), param.getTagIndex(), groupId, param.getFilter(), param.getLimit(), param.getOffset(), param.getSystemTags().values(), session);
		}
		return this.db.getPostsViewable(param.getRequestedGroupName(), param.getUserName(), groupId, HashID.INTRA_HASH, param.getLimit(), param.getOffset(), param.getSystemTags().values(), session);
	}

	@Override
	protected boolean canHandle(final BookmarkParam param) {
		return (present(param.getUserName()) &&
				param.getGrouping() == GroupingEntity.VIEWABLE &&
				present(param.getRequestedGroupName()) &&
				!present(param.getHash()) &&
				nullOrEqual(param.getOrder(), Order.ADDED) &&
				!present(param.getSearch()));
	}
}