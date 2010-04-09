package org.bibsonomy.database.managers.chain.bookmark.get;

import static org.bibsonomy.util.ValidationUtils.nullOrEqual;
import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibTexByConceptByTag;
import org.bibsonomy.database.managers.chain.bookmark.BookmarkChainElement;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.enums.Order;

/**
 * TODO: clone @see {@link GetBibTexByConceptByTag} refactor it using generics
 * 
 * @author Michael Wagner
 * @version $Id$
 */
public class GetBookmarksByConceptByTag extends BookmarkChainElement{

	@Override
	protected List<Post<Bookmark>> handle(BookmarkParam param, DBSession session) {
		return this.db.getPostsByConceptByTag(param.getTagIndex(), param.getLimit(), param.getOffset(), param.getSystemTags().values(), session);
	}

	@Override
	protected boolean canHandle(BookmarkParam param) {
		return (param.getGrouping() == GroupingEntity.ALL &&
				present(param.getTagIndex()) &&
				param.getNumSimpleConcepts() > 0 &&
				param.getNumSimpleTags() == 0 &&
				param.getNumTransitiveConcepts() == 0 &&
				!present(param.getHash()) &&
				nullOrEqual(param.getOrder(), Order.ADDED) &&
				!present(param.getSearch()));
	}
}