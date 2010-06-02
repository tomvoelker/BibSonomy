package org.bibsonomy.database.managers.chain.bookmark.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.bookmark.BookmarkChainElement;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;

/**
 * Returns a list of bookmarks for a given hash.
 * 
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetBookmarksByHash extends BookmarkChainElement {

	@Override
	protected List<Post<Bookmark>> handle(final BookmarkParam param, final DBSession session) {
		return this.db.getPostsByHash(param.getHash(), HashID.getSimHash(param.getSimHash()), GroupID.PUBLIC.getId(), param.getLimit(), param.getOffset(), session);
	}

	@Override
	protected boolean canHandle(final BookmarkParam param) {
		return (present(param.getHash()) &&
				param.getGrouping() == GroupingEntity.ALL &&
				!present(param.getRequestedUserName()) &&
				!present(param.getTagIndex()) &&
				!present(param.getOrder()) &&
				!present(param.getSearch()));
	}
}