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
 * TODO implement compartible method for concept structure
 * 
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetBookmarksByTagNames extends BookmarkChainElement {

	/**
	 * return a list of bookmark by given tag/tags. Following arguments have to
	 * be given:
	 * 
	 * grouping:all name:irrelevant tags:given hash:null popular:false
	 * added:false
	 */
	@Override
	protected List<Post<Bookmark>> handle(final BookmarkParam param, final DBSession session) {
		List<Post<Bookmark>> posts;
		if (param.getTagIndex().size() == 0) {
			posts = db.getBookmarkForHomepage(param, session);
		} else {
			posts = db.getBookmarkByTagNames(param, session);
		}
		return posts;
	}

	@Override
	protected boolean canHandle(final BookmarkParam param) {
		return (param.getGrouping() == GroupingEntity.ALL) && present(param.getTagIndex()) && !present(param.getHash()) && nullOrEqual(param.getOrder(), Order.ADDED);
	}
}