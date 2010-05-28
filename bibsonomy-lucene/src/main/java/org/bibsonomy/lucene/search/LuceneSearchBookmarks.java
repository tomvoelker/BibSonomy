package org.bibsonomy.lucene.search;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.bibsonomy.lucene.param.QuerySortContainer;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResultList;

/**
 * class for bookmark search
 * 
 * @author fei
 *
 */
public class LuceneSearchBookmarks extends LuceneResourceSearch<Bookmark> {
	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(LuceneSearchBookmarks.class);
	
	private final static LuceneSearchBookmarks singleton = new LuceneSearchBookmarks();
	
	/**
	 * constructor
	 */
	private LuceneSearchBookmarks() {
		reloadIndex(0);
	}

	/**
	 * @return LuceneSearchBookmarks
	 */
	public static LuceneSearchBookmarks getInstance() {
		return singleton;
	}


	@Override
	protected QuerySortContainer buildAuthorQuery(String group,
			String searchTerms, String requestedUserName,
			String requestedGroupName, List<String> groupMembers,
			String year, String firstYear, String lastYear, List<String> tagList) {
		throw new UnsupportedOperationException("Author search not available for bookmarks");
	}
	
	@Override
	protected Query buildAuthorSearchQuery(final String autherSearchTerms) {
		// Author search not available for bookmarks
		return new BooleanQuery();
	}
	
	@Override
	protected Class<Bookmark> getResourceType() {
		return Bookmark.class;
	}

	@Override
	protected ResultList<Post<Bookmark>> createEmptyResultList() {
		return new ResultList<Post<Bookmark>>();
	}



}