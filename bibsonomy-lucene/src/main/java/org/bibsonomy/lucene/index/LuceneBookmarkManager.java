package org.bibsonomy.lucene.index;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.lucene.util.LuceneSpringContextWrapper;
import org.bibsonomy.model.Bookmark;
import org.springframework.beans.factory.BeanFactory;

/**
 * class for maintaining the lucene index
 * 
 *  - regularly update the index by looking for new posts
 *  - asynchronously handle requests for flagging/unflagging of spam users
 * 
 * @author fei
 */
public class LuceneBookmarkManager extends LuceneResourceManager<Bookmark> {
	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(LuceneBookmarkManager.class);

	/** spring bean factory for initializing instances */
	private static BeanFactory beanFactory;
	
	/** singleton pattern's class instance */
	private static LuceneBookmarkManager instance;

	/**
	 * static initialization
	 */
	static {
		beanFactory = LuceneSpringContextWrapper.getBeanFactory();
	}
	
	/**
	 * constructor
	 */
	public LuceneBookmarkManager() {
		super();
	}
	
	/**
	 * singleton pattern's instantiation method
	 * 
	 * @return
	 */
	public static LuceneResourceManager<Bookmark> getInstance() {
		if( instance==null ) {
			instance = (LuceneBookmarkManager)beanFactory.getBean("luceneBookmarkManager");
			instance.recovery();
		};
		
		return instance;
	}

	/**
	 * singleton pattern's instantiation method for spring bean initilization 
	 * 
	 * @return
	 */
	public static LuceneBookmarkManager getPreInitInstance() {
		if( instance==null ) {
			instance = new LuceneBookmarkManager();
		};
		
		return instance;
	}
	
}
