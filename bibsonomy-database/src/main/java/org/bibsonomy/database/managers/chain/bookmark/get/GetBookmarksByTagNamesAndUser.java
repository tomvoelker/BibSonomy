package org.bibsonomy.database.managers.chain.bookmark.get;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bookmark.BookmarkChainElement;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.util.Transaction;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;

/**
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetBookmarksByTagNamesAndUser extends BookmarkChainElement {

	/**
	 * return a list of bookmark by given tag/tags and User. Following arguments
	 * have to be given:
	 * 
	 * grouping:User name:given tags:given hash:null popular:false added:false
	 */
	@Override
	protected List<Post<Bookmark>> handle(final BookmarkParam param, final Transaction session) {
		log.debug(this.getClass().getSimpleName());
		param.setGroups(generalDb.getGroupsForUser(param, session));
		return this.db.getBookmarkByTagNamesForUser(param, session);
	}

	@Override
	protected boolean canHandle(final BookmarkParam param) {
		return param.getUserName() != null && param.getGrouping() == GroupingEntity.USER && param.getTagIndex() != null && param.getHash() == null && param.isPopular() == false && param.isAdded() == false;
	}
}