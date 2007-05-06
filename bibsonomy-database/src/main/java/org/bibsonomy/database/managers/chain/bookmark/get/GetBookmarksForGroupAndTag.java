package org.bibsonomy.database.managers.chain.bookmark.get;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bookmark.BookmarkChainElement;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.util.Transaction;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;

public class GetBookmarksForGroupAndTag extends BookmarkChainElement {

	/**
	 * 
	 * @author mgr
	 * 
	 */

	/*
	 * return a list of bookmark by a given group and common tags of a group.
	 * Following arguments have to be given:
	 * 
	 * grouping:group name:given tags:given hash:null popular:false added:false
	 * 
	 */
	@Override
	protected List<Post<Bookmark>> handle(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end, final Transaction session) {
		final BookmarkParam param = new BookmarkParam();

		param.setRequestedGroupName(groupingName);
		param.setUserName(authUser);
		param.setOffset(start);
		int limit = end - start;
		param.setLimit(limit);

		for (String tag : tags) {
			param.addTagName(tag);
		}

		param.setGroupId(generalDb.getGroupIdByGroupName(param, session));
		param.setGroups(generalDb.getGroupsForUser(param, session));

		List<Post<Bookmark>> posts = db.getBookmarkForGroupByTag(param, session);
		if (posts.size() != 0) {
			System.out.println("GetBookmarksByGroupAndTag");
		}
		return posts;
	}

	/*
	 * prove arguments as mentioned above
	 */
	@Override
	protected boolean canHandle(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end) {
		return authUser != null && grouping == GroupingEntity.GROUP && groupingName != null && tags != null && hash == null && popular == false && added == false;
	}

}