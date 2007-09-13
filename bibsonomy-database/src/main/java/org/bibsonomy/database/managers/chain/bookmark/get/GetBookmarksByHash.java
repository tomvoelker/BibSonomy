package org.bibsonomy.database.managers.chain.bookmark.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bookmark.BookmarkChainElement;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;

/**
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetBookmarksByHash extends BookmarkChainElement {

	/**
	 * return a list of bookmark by a given hash. Following arguments have to be
	 * given:
	 */
	@Override
	protected List<Post<Bookmark>> handle(final BookmarkParam param, final DBSession session) {
		return this.db.getBookmarkByHash(param, session);
	}

	@Override
	protected boolean canHandle(final BookmarkParam param) {
		return present(param.getHash()) && (param.getGrouping() == GroupingEntity.ALL) && !present(param.getTagIndex()) && !present(param.getOrder());
	}
}