package org.bibsonomy.database.managers.chain.bookmark.get;

import static org.bibsonomy.util.ValidationUtils.nullOrEqual;
import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.database.managers.chain.bookmark.BookmarkChainElement;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.enums.Order;

/**
 * Return a list of popular bookmarks.
 * 
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetBookmarksPopular extends BookmarkChainElement {

	@Override
	protected List<Post<Bookmark>> handle(final BookmarkParam param, final DBSession session) {
		return this.db.getPostsPopular(param.getDays(), param.getLimit(), param.getOffset(), HashID.getSimHash(param.getSimHash()), session);
	}

	@Override
	protected boolean canHandle(final BookmarkParam param) {
		return (param.getGrouping() == GroupingEntity.ALL &&
				param.getDays() >= 0 &&
				!present(param.getHash()) &&
				nullOrEqual(param.getOrder(), Order.POPULAR) &&
				!present(param.getSearch()) &&
				!present(param.getTitle()));
	}
}