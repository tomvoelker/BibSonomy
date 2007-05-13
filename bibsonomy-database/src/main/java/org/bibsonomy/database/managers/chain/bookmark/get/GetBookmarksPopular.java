package org.bibsonomy.database.managers.chain.bookmark.get;

import java.util.List;

import org.bibsonomy.database.managers.chain.bookmark.BookmarkChainElement;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.util.Transaction;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;

/**
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetBookmarksPopular extends BookmarkChainElement {

	/**
	 * return a list of bookmark by a logged user. Following arguments have to
	 * be given:
	 * 
	 * grouping:irrelevant name:irrelevant tags:irrelevant hash:irrelevant
	 * popular:true added:false
	 */
	@Override
	protected List<Post<Bookmark>> handle(final BookmarkParam param, final Transaction session) {
		log.debug(this.getClass().getSimpleName());
		return this.db.getBookmarkPopular(param, session);
	}

	@Override
	protected boolean canHandle(final BookmarkParam param) {
		return param.isPopular() == true && param.isAdded() == false;
	}
}