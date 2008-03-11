package org.bibsonomy.database.managers.chain.bookmark.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.GroupID;
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
public class GetBookmarksForGroupAndTag extends BookmarkChainElement {

	/**
	 * return a list of bookmark by a given group and common tags of a group.
	 * Following arguments have to be given:
	 */
	@Override
	protected List<Post<Bookmark>> handle(final BookmarkParam param, final DBSession session) {
		final Integer groupId = this.generalDb.getGroupIdByGroupName(param, session);
		if (groupId == GroupID.INVALID.getId()  || GroupID.isSpecialGroupId(groupId)) {
			log.debug("groupId " +  param.getRequestedGroupName() + " not found or special group" );
			return new ArrayList<Post<Bookmark>>(0);			
		}
		param.setGroupId(groupId);	
		return this.db.getBookmarkForGroupByTag(param, session);
	}

	@Override
	protected boolean canHandle(final BookmarkParam param) {
		return 	(param.getGrouping() == GroupingEntity.GROUP) && 
				present(param.getRequestedGroupName()) && 
				present(param.getTagIndex()) && 
				!present(param.getHash()) && 
				!present(param.getOrder()) && 
				!present(param.getSearch()) &&
				(param.getNumSimpleConcepts() == 0);
	}
}