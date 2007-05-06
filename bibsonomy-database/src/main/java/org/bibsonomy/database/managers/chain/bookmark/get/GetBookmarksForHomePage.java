package org.bibsonomy.database.managers.chain.bookmark.get;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bookmark.BookmarkChainElement;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.util.Transaction;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;

public class GetBookmarksForHomePage extends BookmarkChainElement {

	/**
	 * 
	 * @author mgr
	 * 
	 */

	/*
	 * return a list of bookmark by a logged user. Following arguments have to
	 * be given:
	 * 
	 * grouping:null name:irrelevant tags:irrelevant hash:irrelevant
	 * popular:false added:true
	 * 
	 */
	@Override
	protected List<Post<Bookmark>> handle(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end, final Transaction session) {
		final BookmarkParam param = new BookmarkParam();
		param.setOffset(start);
		int limit = end - start;
		param.setLimit(limit);
		/**
		 * retrieve bookmark list with appropriate iBatis statement
		 */
		List<Post<Bookmark>> posts = db.getBookmarkForHomepage(param, session);
		System.out.println("post=" + posts.size() + "in getBookmarkForHomepage");
		return posts;
	}

	/*
	 * prove arguments as mentioned above
	 */
	@Override
	protected boolean canHandle(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end) {
		return grouping == null && popular == false && added == false;
	}

}