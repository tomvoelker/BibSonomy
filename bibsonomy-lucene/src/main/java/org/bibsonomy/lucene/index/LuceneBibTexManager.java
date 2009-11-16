package org.bibsonomy.lucene.index;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.BibTex;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * class for maintaining the lucene index
 * 
 *  - regularly update the index by looking for new posts
 *  - asynchronously handle requests for flagging/unflagging of spam users
 * 
 * @author fei
 */
public class LuceneBibTexManager extends LuceneResourceManager<BibTex> {
	private static final Log log = LogFactory.getLog(LuceneBibTexManager.class);

	/** spring bean factory for initializing instances */
	private static BeanFactory beanFactory;
	
	/** singleton pattern's class instance */
	private static LuceneBibTexManager instance;

	/**
	 * static initialization
	 */
	static {
		ApplicationContext context = new ClassPathXmlApplicationContext(
		        new String[] {"LuceneContext.xml"});

		// an ApplicationContext is also a BeanFactory (via inheritance)
		beanFactory = context;
	}
	
	/**
	 * constructor
	 */
	public LuceneBibTexManager() {
		super();
	}
	
	/**
	 * singleton pattern's instantiation method
	 * 
	 * @return
	 */
	public static LuceneBibTexManager getInstance() {
		if( instance==null ) {
			instance = (LuceneBibTexManager)beanFactory.getBean("luceneBibTexManager");
		};
		
		return instance;
	}
	
	/**
	 * singleton pattern's instantiation method for spring bean initilization 
	 * 
	 * @return
	 */
	public static LuceneBibTexManager getPreInitInstance() {
		if( instance==null ) {
			instance = new LuceneBibTexManager();
		};
		
		return instance;
	}
}
