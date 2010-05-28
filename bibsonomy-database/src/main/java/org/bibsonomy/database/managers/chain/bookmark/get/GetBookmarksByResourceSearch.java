package org.bibsonomy.database.managers.chain.bookmark.get;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.database.managers.chain.resource.get.GetResourcesByResourceSearch;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.model.Bookmark;

/**
 * Returns a list of BibTex's for a given search.
 * 
 * @author claus
 * @version $Id$
 */
public class GetBookmarksByResourceSearch extends GetResourcesByResourceSearch<Bookmark, BookmarkParam> {

	@Override
	protected boolean canHandle(final BookmarkParam param) {
		return (present(param.getSearch()) || present(param.getTitle())); 
	}	
}