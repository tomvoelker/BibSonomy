package org.bibsonomy.lucene.database;

import org.bibsonomy.lucene.database.params.BookmarkParam;
import org.bibsonomy.lucene.database.params.ResourcesParam;
import org.bibsonomy.model.Bookmark;

/**
 * class for accessing the bibsonomy database 
 * 
 * @author fei
 * @version $Id$
 */
public class LuceneBookmarkLogic extends LuceneDBLogic<Bookmark> {
	/** singleton pattern's instance reference */
	protected static LuceneDBLogic<Bookmark> instance = null;
	
	/**
	 * @return An instance of this implementation of {@link LuceneDBInterface}
	 */
	public static LuceneDBInterface<Bookmark> getInstance() {
		if (instance == null) {
			instance = new LuceneBookmarkLogic();
		}
		
		return instance;
	}
	
	/**
	 * constructor disabled for singleton pattern 
	 */
	private LuceneBookmarkLogic() {
	}
	
	@Override
	protected ResourcesParam<Bookmark> getResourcesParam() {
		return new BookmarkParam();
	}
	
	@Override
	protected String getResourceName() {
		return Bookmark.class.getSimpleName();
	}
}
