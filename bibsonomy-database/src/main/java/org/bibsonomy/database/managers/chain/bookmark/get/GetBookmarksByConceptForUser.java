package org.bibsonomy.database.managers.chain.bookmark.get;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bookmark.BookmarkChainElement;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.util.Transaction;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;

public class GetBookmarksByConceptForUser extends BookmarkChainElement {

	/**
	 * 
	 * @author mgr
	 * 
	 */

	/*
	 * return a list of bookmarks by a tag-concept. All bookmarks will be return
	 * for a given "super-tag". Following arguments have to be given:
	 * 
	 * grouping:user name:given tags:given hash:null popular:false added:true
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

		param.setGroups(generalDb.getGroupsForUser(param, transaction));

		for (String tag : tags) {

			param.addTagName(tag);

		}

		List<Post<Bookmark>> posts = db.getBookmarkByConceptForUser(param, transaction);
		if (posts.size() != 0) {
			System.out.println("GetBookmarksByConceptForUser");

		}
		return posts;
	}

	@Override
	protected boolean canHandle(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end) {
		return authUser != null && grouping == GroupingEntity.USER && groupingName != null && tags != null && hash == null && popular == false && added == true;
	}

}
