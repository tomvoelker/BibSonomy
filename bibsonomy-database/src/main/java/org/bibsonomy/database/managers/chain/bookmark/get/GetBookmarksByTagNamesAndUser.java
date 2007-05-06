package org.bibsonomy.database.managers.chain.bookmark.get;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bookmark.BookmarkChainElement;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.util.Transaction;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;

public class GetBookmarksByTagNamesAndUser extends BookmarkChainElement {
	/**
	 * 
	 * @author mgr
	 * 
	 */

	/*
	 * return a list of bookmark by given tag/tags and User. Following arguments
	 * have to be given:
	 * 
	 * grouping:User name:given tags:given hash:null popular:false added:false
	 * 
	 */
	@Override
	protected List<Post<Bookmark>> handle(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end, final Transaction session) {
		final BookmarkParam param = new BookmarkParam();
		param.setRequestedUserName(groupingName);
		param.setUserName(authUser);

		param.setOffset(start);
		int limit = end - start;
		param.setLimit(limit);
		param.setGroups(generalDb.getGroupsForUser(param, session));

		for (String tag : tags) {
			param.addTagName(tag);
		}

		List<Post<Bookmark>> posts = db.getBookmarkByTagNamesForUser(param, session);

		if (posts.size() != 0) {
			System.out.println("GetBookmarksByTagNamesAndUser");
		}
		return posts;
	}

	/*
	 * prove arguments as mentioned above
	 */
	@Override
	protected boolean canHandle(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end) {
		return authUser != null && grouping == GroupingEntity.USER && tags != null && hash == null && popular == false && added == false;
	}

}
