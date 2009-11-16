package org.bibsonomy.lucene.index;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Resource;

/**
 * class for managing the lucene bookmark index
 * 
 * @author fei
 *
 */
public class LuceneBookmarkIndex extends LuceneResourceIndex<Bookmark> {
	/** singleton instance */
	protected static LuceneResourceIndex<Bookmark> instance;
	

	@Override
	protected Class<? extends Resource> getResourceType() {
		return Bookmark.class;
	}

	/**
	 * singleton pattern
	 * @return
	 */
	public static LuceneResourceIndex<Bookmark> getInstance() {
		if (instance == null) instance = new LuceneBookmarkIndex();
		return instance;
	}

}
