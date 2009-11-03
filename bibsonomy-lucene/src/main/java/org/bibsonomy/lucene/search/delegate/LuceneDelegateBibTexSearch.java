package org.bibsonomy.lucene.search.delegate;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.lucene.index.LuceneBookmarkManager;
import org.bibsonomy.lucene.index.LuceneResourceManager;
import org.bibsonomy.lucene.search.LuceneResourceSearch;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.ResultList;
import org.bibsonomy.model.User;
import org.bibsonomy.services.searcher.ResourceSearch;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * parent class for lucene search, coupling index management (spam) and search by delegation
 * 
 * FIXME: split ResourceSearch interface in search and index management tasks - this will clean up this package
 * 
 * @author fei
 *
 */
public class LuceneDelegateBibTexSearch extends LuceneDelegateResourceSearch<BibTex> {
	private final static Log log = LogFactory.getLog(LuceneDelegateBibTexSearch.class);

	/** spring bean factory for initializing instances */
	private static BeanFactory beanFactory;
	
	/** singleton pattern's class instance */
	private static LuceneDelegateResourceSearch<BibTex> instance;

	//------------------------------------------------------------------------
	// singleton pattern implementation
	//------------------------------------------------------------------------
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
	 * singleton pattern's instantiation method
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static LuceneDelegateResourceSearch<BibTex> getInstance() {
		if( instance==null ) {
			instance = (LuceneDelegateResourceSearch<BibTex>)beanFactory.getBean("luceneDelegateBibTexSearch");
		};
		
		return instance;
	}

	/**
	 * singleton pattern's instantiation method for spring bean initilization 
	 * 
	 * @return
	 */
	public static LuceneDelegateResourceSearch<BibTex> getPreInitInstance() {
		if( instance==null ) {
			instance = new LuceneDelegateBibTexSearch();
		};
		
		return instance;
	}}
