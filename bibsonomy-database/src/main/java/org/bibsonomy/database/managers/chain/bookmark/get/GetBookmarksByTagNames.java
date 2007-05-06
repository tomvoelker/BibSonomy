package org.bibsonomy.database.managers.chain.bookmark.get;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bookmark.BookmarkChainElement;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.util.Transaction;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;

public class GetBookmarksByTagNames extends BookmarkChainElement {
	/**
	 * 
	 * @author mgr
	 * 
	 */

	/*
	 * return a list of bookmark by given tag/tags. Following arguments have to
	 * be given:
	 * 
	 * grouping:all name:irrelevant tags:given hash:null popular:false
	 * added:false
	 * 
	 */
	@Override
	protected List<Post<Bookmark>> handle(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end, final Transaction session) {
		final BookmarkParam param = new BookmarkParam();
		param.setOffset(start);
		int limit = end - start;
		param.setLimit(limit);

		for (String tag : tags) {
			param.addTagName(tag);
		}

		/*
		 * TODO implement compartible method for concept structure
		 */
		/*
		 * prove arguments as mentioned above
		 */
		List<Post<Bookmark>> posts = db.getBookmarkByTagNames(param, session);
		if (posts.size() != 0) {
			System.out.println("GetBookmarksByTagNames");
		}
		return posts;

	}

	@Override
	protected boolean canHandle(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end) {
		return grouping == GroupingEntity.ALL && tags != null && hash == null && popular == false && added == false;
	}

}
