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
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.enums.Order;

/**
 * Returns a list of bookmarks for a given friend of a user (this friends also
 * posted this bookmarks to group friends (made bookmarks viewable for friends))
 * restricted by a given tag.
 * 
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetBookmarksOfFriendsByTags extends BookmarkChainElement {

	@Override
	protected List<Post<Bookmark>> handle(final BookmarkParam param, final DBSession session) {
		/*
		 * if the requested user has the current user in his/her friend list, he may 
		 * see the posts
		 */
		if (this.generalDb.isFriendOf(param.getUserName(), param.getRequestedUserName(), session)) {
			return this.db.getPostsByTagNamesForUser(param.getUserName(), param.getRequestedUserName(), param.getTagIndex(), GroupID.FRIENDS.getId(), param.getGroups(), param.getLimit(), param.getOffset(), param.getFilter(), param.getSystemTags().values(), session);
		}
		return new ArrayList<Post<Bookmark>>();
	}

	@Override
	protected boolean canHandle(final BookmarkParam param) {
		return (present(param.getUserName()) &&
				param.getGrouping() == GroupingEntity.FRIEND &&
				present(param.getRequestedUserName()) &&
				present(param.getTagIndex()) &&
				param.getNumSimpleConcepts() == 0 &&
				param.getNumSimpleTags() > 0 &&
				param.getNumTransitiveConcepts() == 0 &&
				!present(param.getHash()) &&
				nullOrEqual(param.getOrder(), Order.ADDED) &&
				!present(param.getSearch()) &&
				!present(param.getTitle()) &&
				!present(param.getAuthor()));
	}
}