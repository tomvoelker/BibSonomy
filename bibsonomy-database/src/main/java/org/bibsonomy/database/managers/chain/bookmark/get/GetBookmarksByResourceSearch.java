package org.bibsonomy.database.managers.chain.bookmark.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.bookmark.BookmarkChainElement;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.util.DatabaseUtils;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;

/**
 * Returns a list of BibTex's for a given search.
 * 
 * @author claus
 * @version $Id$
 */
public class GetBookmarksByResourceSearch extends BookmarkChainElement {

	@Override
	protected List<Post<Bookmark>> handle(final BookmarkParam param, DBSession session) {
		// convert tag index to tag list
		List<String> tagIndex = null;
		if (present(param.getTagIndex())) {
			tagIndex = DatabaseUtils.extractTagNames(param.getTagIndex());
		}
		
		// query the resource searcher
		return this.db.getPostsByResourceSearch(
				param.getUserName(), param.getRequestedUserName(), param.getRequestedGroupName(), 
				param.getGroupNames(), param.getRawSearch(), param.getTitle(), param.getAuthor(), tagIndex, 
				null, null, null, 
				param.getLimit(), param.getOffset());
	}

	@Override
	protected boolean canHandle(final BookmarkParam param) {
		return (present(param.getSearch()) || present(param.getTitle())); 
	}
}