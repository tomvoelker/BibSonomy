package org.bibsonomy.database.managers.chain.bookmark.get;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bookmark.BookmarkChainElement;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.util.Transaction;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;

/*
 * TODO check
 */
public class GetBookmarksByFriends extends BookmarkChainElement {

	/**
	 * 
	 * @author mgr
	 * 
	 */

	/*
	 * 
	 * TODO extension with user restriction rearding returned bookmarks and
	 * appropriate naming of URL in REST interface
	 * 
	 * grouping:friend name:given tags:NULL hash:NULL popular:false added:false
	 * /user/friend
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

		List<Post<Bookmark>> posts = db.getBookmarkByUserFriends(param, transaction);

		return posts;
	}

	@Override
	/*
	 * prove arguments as mentioned above
	 */
	/*
	 * TODO username: semantik fehlt in API
	 */
	protected boolean canHandle(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end) {
		return authUser != null && grouping == GroupingEntity.FRIEND && groupingName != null && (tags == null || tags.size() == 0) && hash == null && popular == false && added == false;
	}

}
