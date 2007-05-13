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
public class GetBookmarksByConceptForUser extends BookmarkChainElement {

	/**
	 * return a list of bookmarks by a tag-concept. All bookmarks will be return
	 * for a given "super-tag". Following arguments have to be given:
	 * 
	 * grouping:user name:given tags:given hash:null popular:false added:true
	 */
	@Override
	protected List<Post<Bookmark>> handle(final BookmarkParam param, final Transaction session) {
		log.debug(this.getClass().getSimpleName());
		param.setGroups(this.generalDb.getGroupsForUser(param, session));
		return this.db.getBookmarkByConceptForUser(param, session);
	}

	@Override
	protected boolean canHandle(final BookmarkParam param) {
		return param.getUserName() != null && param.getGrouping() == GroupingEntity.USER && param.getRequestedGroupName() != null && param.getTagIndex() != null && param.getHash() == null && param.isPopular() == false && param.isAdded() == true;
	}
}