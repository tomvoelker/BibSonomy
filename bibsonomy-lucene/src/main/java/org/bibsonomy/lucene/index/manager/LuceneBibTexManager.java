package org.bibsonomy.lucene.index.manager;

import org.bibsonomy.lucene.util.LuceneSpringContextWrapper;
import org.bibsonomy.model.BibTex;

/**
 * class for maintaining the lucene index
 * 
 *  - regularly update the index by looking for new posts
 *  - asynchronously handle requests for flagging/unflagging of spam users
 * 
 * @author fei
 * @version $Id$
 */
public class LuceneBibTexManager extends LuceneResourceManager<BibTex> {
	
	/** singleton pattern's class instance */
	private static LuceneBibTexManager instance;
	
	/**
	 * singleton pattern's instantiation method
	 * 
	 * @return the {@link LuceneBookmarkManager} instance 
	 */
	public static LuceneBibTexManager getInstance() {
		if (instance == null) {
			instance = new LuceneBibTexManager();
			LuceneSpringContextWrapper.init();
			instance.recovery();
		}
		
		return instance;
	}
	
	/**
	 * constructor
	 */
	private LuceneBibTexManager() {
	}
}
