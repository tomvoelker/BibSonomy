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
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetBookmarksForGroup extends BookmarkChainElement {

	/**
	 * return a list of bookmark entries by a given group
	 */
	@Override
	protected List<Post<Bookmark>> handle(final BookmarkParam param, final DBSession session) {
		// param.setGroupId(this.generalDb.getGroupIdByGroupNameAndUserName(param, session));
		param.setGroupId(this.generalDb.getGroupIdByGroupName(param, session));
		// TODO: is this needed?  param.setGroups(this.generalDb.getGroupsForUser(param, session));

		return this.db.getBookmarkForGroup(param, session);
	}

	@Override
	protected boolean canHandle(final BookmarkParam param) {		
		return (param.getGrouping() == GroupingEntity.GROUP) && present(param.getRequestedGroupName()) &&  !present(param.getTagIndex()) && !present(param.getHash()) && !present(param.getOrder()) && !present(param.getSearch());
	}
}