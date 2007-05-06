package org.bibsonomy.database.managers.chain.bookmark.get;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bookmark.BookmarkChainElement;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.util.Transaction;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;

public class GetBookmarksForGroup extends BookmarkChainElement {

	/**
	 * 
	 * @author mgr
	 * 
	 */

	/*
	 * return a list of bookmark by a given group. Following arguments have to
	 * be given:
	 * 
	 * grouping:group name:given tags:NULL hash:null popular:false added:false
	 * 
	 */
	@Override
	protected List<Post<Bookmark>> handle(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end, final Transaction transaction) {
		final BookmarkParam param = new BookmarkParam();

		param.setRequestedGroupName(groupingName);
		param.setUserName(authUser);
		param.setOffset(start);
		int limit = end - start;
		param.setLimit(limit);

		param.setGroupId(generalDb.getGroupIdByGroupName(param, transaction));
		param.setGroups(generalDb.getGroupsForUser(param, transaction));

		List<Post<Bookmark>> posts = db.getBookmarkForGroup(param, transaction);
		if (posts.size() != 0) {
			System.out.println("GetBookmarksByGroup");

		}
		return posts;
	}

	/*
	 * prove arguments as mentioned above
	 */
	@Override
	protected boolean canHandle(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end) {
		return authUser != null && grouping == GroupingEntity.GROUP && groupingName != null && (tags == null || tags.size() == 0) && hash == null && popular == false && added == false;
	}

}