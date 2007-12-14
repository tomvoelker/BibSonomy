package org.bibsonomy.database.managers.chain.bookmark.get;

import static org.bibsonomy.util.ValidationUtils.nullOrEqual;
import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bookmark.BookmarkChainElement;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.logic.Order;

/**
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetBookmarksViewable extends BookmarkChainElement {

	/**
	 * return a list of bookmark by a given group (which is only viewable for
	 * groupmembers excluded public option regarding setting a post)
	 */
	@Override
	protected List<Post<Bookmark>> handle(final BookmarkParam param, final DBSession session) {
		final Integer groupId;
		try {
			final GroupID specialGroup = GroupID.getSpecialGroup(param.getRequestedGroupName());
		
			if (specialGroup != null) {
				groupId = specialGroup.getId();
			} else {
				groupId = this.generalDb.getGroupIdByGroupNameAndUserName(param, session);
			}
			if (groupId == null) {
				log.debug("groupId not found");
				return new ArrayList<Post<Bookmark>>(0);
			}
		}
		catch (IllegalArgumentException e) {
			log.debug("groupId not found");
			return new ArrayList<Post<Bookmark>>(0);
		}
		log.debug("groupId=" + groupId);
		param.setGroupId(groupId);	
		
		// param.setGroupId(this.generalDb.getGroupIdByGroupNameAndUserName(param, session));
		// TODO: is this needed?  param.setGroups(this.generalDb.getGroupsForUser(param, session));

		return this.db.getBookmarkViewable(param, session);
	}

	@Override
	protected boolean canHandle(final BookmarkParam param) {
		return present(param.getUserName()) && (param.getGrouping() == GroupingEntity.VIEWABLE) && present(param.getRequestedGroupName()) && !present(param.getTagIndex()) && !present(param.getHash()) && nullOrEqual(param.getOrder(), Order.ADDED) && !present(param.getSearch());
	}
}