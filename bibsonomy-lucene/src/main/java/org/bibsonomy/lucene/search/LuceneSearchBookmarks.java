package org.bibsonomy.lucene.search;

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
	protected String getResourceName() {
		return Bookmark.class.getSimpleName();
	}

}