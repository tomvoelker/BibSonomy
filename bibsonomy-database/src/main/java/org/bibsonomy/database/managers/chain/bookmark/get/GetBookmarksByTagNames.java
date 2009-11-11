package org.bibsonomy.database.managers.chain.bookmark.get;

import static org.bibsonomy.util.ValidationUtils.nullOrEqual;
import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bookmark.BookmarkChainElement;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.enums.Order;

/**
 * Returns a list of bookmarks for given tag/tags.
 * 
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetBookmarksByTagNames extends BookmarkChainElement {

	@Override
	protected List<Post<Bookmark>> handle(final BookmarkParam param, final DBSession session) {
		final List<Post<Bookmark>> posts;
		if (param.getTagIndex().isEmpty()) {
			posts = this.db.getPostsForHomepage(param.getFilter(), param.getLimit(), param.getOffset(), param.getSystemTags().values(), session);
		} else {
			posts = this.db.getPostsByTagNames(param.getGroupId(), param.getTagIndex(), param.getOrder(), param.getLimit(), param.getOffset(), session);
		}
		return posts;
	}

	@Override
	protected boolean canHandle(final BookmarkParam param) {
		return (param.getGrouping() == GroupingEntity.ALL &&
				present(param.getTagIndex()) &&
				param.getNumSimpleConcepts() == 0 &&
				param.getNumSimpleTags() > 0 &&
				param.getNumTransitiveConcepts() == 0 &&
				!present(param.getHash()) &&
				nullOrEqual(param.getOrder(), Order.ADDED, Order.FOLKRANK) &&
				!present(param.getSearch()));
	}
}