package org.bibsonomy.lucene.search;

import java.util.List;

import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.bibsonomy.lucene.param.QuerySortContainer;
import org.bibsonomy.model.Bookmark;

/**
 * class for bookmark search
 * 
 * @author fei
 * @version $Id$
 */
public class LuceneSearchBookmarks extends LuceneResourceSearch<Bookmark> {	
	private final static LuceneSearchBookmarks singleton = new LuceneSearchBookmarks();

	/**
	 * @return LuceneSearchBookmarks
	 */
	public static LuceneSearchBookmarks getInstance() {
		return singleton;
	}
	
	/**
	 * constructor
	 */
	private LuceneSearchBookmarks() {
		reloadIndex(0);
	}
	
	@Override
	protected QuerySortContainer buildAuthorQuery(String group, String searchTerms, String requestedUserName, String requestedGroupName, List<String> groupMembers, String year, String firstYear, String lastYear, List<String> tagList) {
		throw new UnsupportedOperationException("Author search not available for bookmarks");
	}
	
	@Override
	protected Query buildAuthorSearchQuery(final String autherSearchTerms) {
		// Author search not available for bookmarks
		return new BooleanQuery();
	}
	
	@Override
	protected String getResourceName() {
		return Bookmark.class.getSimpleName();
	}

}