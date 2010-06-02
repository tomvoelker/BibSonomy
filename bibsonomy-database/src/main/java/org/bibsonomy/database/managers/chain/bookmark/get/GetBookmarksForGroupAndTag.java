package org.bibsonomy.database.managers.chain.bookmark.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.bookmark.BookmarkChainElement;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;

/**
 * Returns a list of bookmarks for a given group and common tags of a group.
 * 
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetBookmarksForGroupAndTag extends BookmarkChainElement {

	/**
	 */
	@Override
	protected List<Post<Bookmark>> handle(final BookmarkParam param, final DBSession session) {
		final Integer groupId = this.groupDb.getGroupIdByGroupName(param.getRequestedGroupName(), session);
		if (groupId == GroupID.INVALID.getId() || GroupID.isSpecialGroupId(groupId)) {
			log.debug("groupId " + param.getRequestedGroupName() + " not found or special group");
			return new ArrayList<Post<Bookmark>>(0);
		}
		
		return this.db.getPostsForGroupByTag(groupId, param.getGroups(), param.getUserName(), param.getTagIndex(), null, param.getLimit(), param.getOffset(), null, session);
	}

	@Override
	protected boolean canHandle(final BookmarkParam param) {
		return (param.getGrouping() == GroupingEntity.GROUP &&
				present(param.getRequestedGroupName()) &&
				present(param.getTagIndex()) &&
				!present(param.getHash()) &&
				!present(param.getOrder()) &&
				!present(param.getSearch()) &&
				!present(param.getTitle()) &&
				!present(param.getAuthor()) &&
				(param.getNumSimpleConcepts() == 0));
	}
}