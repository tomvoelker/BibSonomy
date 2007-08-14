package org.bibsonomy.database.managers.chain.bookmark.get;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bookmark.BookmarkChainElement;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.logic.Order;

/**
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetBookmarksForHomePage extends BookmarkChainElement {

	/**
	 * return a list of bookmark by a logged user. Following arguments have to
	 * be given:
	 * 
	 * grouping:null name:irrelevant tags:irrelevant hash:irrelevant
	 * popular:false added:true
	 */
	@Override
	protected List<Post<Bookmark>> handle(final BookmarkParam param, final DBSession session) {
		return this.db.getBookmarkForHomepage(param, session);
	}

	@Override
	protected boolean canHandle(final BookmarkParam param) {
		return present(param.getGrouping()) && param.getGrouping() == GroupingEntity.ALL && !present(param.getTagIndex()) && !present(param.getHash()) && nullOrEqual(param.getOrder(), Order.ADDED);
	}
}