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
 * Returns a list of bookmarks for a concept.
 * 
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetBookmarksByConceptForUser extends BookmarkChainElement {

	@Override
	protected List<Post<Bookmark>> handle(final BookmarkParam param, final DBSession session) {
		return this.db.getPostsByConceptForUser(param.getUserName(), param.getRequestedUserName(), param.getGroups(), param.getTagIndex(), param.getLimit(), param.getOffset(), session);
	}

	@Override
	protected boolean canHandle(final BookmarkParam param) {
		return (param.getGrouping() == GroupingEntity.USER &&
				present(param.getRequestedUserName()) &&
				present(param.getTagIndex()) &&
				param.getNumSimpleConcepts() > 0 &&
				param.getNumSimpleTags() == 0 &&
				param.getNumTransitiveConcepts() == 0 &&
				!present(param.getHash()) &&
				nullOrEqual(param.getOrder(), Order.ADDED) &&
				!present(param.getSearch()));
	}
}