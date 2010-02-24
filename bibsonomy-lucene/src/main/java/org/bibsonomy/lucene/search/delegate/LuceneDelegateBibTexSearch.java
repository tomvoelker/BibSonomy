package org.bibsonomy.lucene.search.delegate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.lucene.util.LuceneSpringContextWrapper;
import org.bibsonomy.model.BibTex;
import org.springframework.beans.factory.BeanFactory;

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
		beanFactory = LuceneSpringContextWrapper.getBeanFactory();
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
		}
		
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
		}
		
		return instance;
	}}
