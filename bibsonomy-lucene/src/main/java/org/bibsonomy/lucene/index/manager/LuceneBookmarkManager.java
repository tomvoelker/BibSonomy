package org.bibsonomy.lucene.index.manager;

import org.bibsonomy.lucene.util.LuceneSpringContextWrapper;
import org.bibsonomy.model.Bookmark;

/**
 * class for maintaining the lucene index
 * 
 *  - regularly update the index by looking for new posts
 *  - asynchronously handle requests for flagging/unflagging of spam users
 * 
 * @author fei
 * @version $Id$
 */
public class LuceneBookmarkManager extends LuceneResourceManager<Bookmark> {
	
	/** singleton pattern's class instance */
	private static LuceneBookmarkManager instance;
	
	/**
	 * singleton pattern's instantiation method
	 * 
	 * @return TODODZ
	 */
	public static LuceneResourceManager<Bookmark> getInstance() {
		if (instance == null) {
			instance = new LuceneBookmarkManager();
			LuceneSpringContextWrapper.init();
			instance.recovery();
		}
		
		return instance;
	}
	
	/**
	 * constructor
	 */
	private LuceneBookmarkManager() {
	}	
}
