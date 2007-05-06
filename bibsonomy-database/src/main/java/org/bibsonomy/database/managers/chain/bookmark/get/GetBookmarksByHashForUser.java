package org.bibsonomy.database.managers.chain.bookmark.get;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bookmark.BookmarkChainElement;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.util.Transaction;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;

public class GetBookmarksByHashForUser extends BookmarkChainElement {

	/**
	 * 
	 * @author mgr
	 * 
	 */

	/*
	 * return a list of bookmark by a given hash and a logged user. Following
	 * arguments have to be given:
	 * 
	 * grouping:user name:given tags:NULL hash:given popular:false added:false
	 * 
	 */
	@Override
	protected List<Post<Bookmark>> handle(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end, final Transaction transaction) {
		final BookmarkParam param = new BookmarkParam();
		param.setRequestedUserName(groupingName);
		param.setUserName(authUser);
		param.setHash(hash);

		param.setOffset(start);
		int limit = end - start;
		param.setLimit(limit);

		param.setGroups(generalDb.getGroupsForUser(param, transaction));

		/**
		 * retrieve bookmark list with appropriate iBatis statement
		 */
		List<Post<Bookmark>> posts = db.getBookmarkByHashForUser(param, transaction);
		if (posts.size() != 0) {
			System.out.println("GetBookmarksByHashForUser");
		}
		return posts;
	}

	/*
	 * prove arguments as mentioned above
	 */
	@Override
	protected boolean canHandle(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end) {
		return hash != null && hash.length() > 0 && authUser != null && grouping == GroupingEntity.USER && groupingName != null && (tags == null || tags.size() == 0) && popular == false && added == false;
	}

}