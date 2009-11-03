package org.bibsonomy.lucene.index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.index.CorruptIndexException;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.lucene.database.LuceneDBInterface;
import org.bibsonomy.lucene.param.LuceneData;
import org.bibsonomy.lucene.search.delegate.LuceneDelegateResourceSearch;
import org.bibsonomy.lucene.util.Utils;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.FileSystemResource;

/**
 * class for maintaining the lucene index
 * 
 *  - regularly update the index by looking for new posts
 *  - asynchronously handle requests for flagging/unflagging of spam users
 * 
 * @author fei
 */
public class LuceneBookmarkManager extends LuceneResourceManager<Bookmark> {
	private static final Log log = LogFactory.getLog(LuceneBookmarkManager.class);

	/** spring bean factory for initializing instances */
	private static BeanFactory beanFactory;
	
	/** singleton pattern's class instance */
	private static LuceneBookmarkManager instance;

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
