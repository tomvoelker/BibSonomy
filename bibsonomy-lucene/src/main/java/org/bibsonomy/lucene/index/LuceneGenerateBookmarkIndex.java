package org.bibsonomy.lucene.index;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.lucene.index.CorruptIndexException;
import org.bibsonomy.lucene.util.JNDITestDatabaseBinder;
import org.bibsonomy.lucene.util.LuceneSpringContextWrapper;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Resource;
import org.springframework.beans.factory.BeanFactory;

public class LuceneGenerateBookmarkIndex extends LuceneGenerateResourceIndex<Bookmark> {
	/** singleton pattern */
	private static LuceneGenerateBookmarkIndex instance;
	
	/** bean factory for obtaining instances configured via spring */
	private static BeanFactory beanFactory;
	
	//------------------------------------------------------------------------
	// constructor
	//------------------------------------------------------------------------
	public LuceneGenerateBookmarkIndex(Properties props)
	throws ClassNotFoundException, SQLException {
		super(props);
		// TODO Auto-generated constructor stub
	}

	public LuceneGenerateBookmarkIndex()
	throws ClassNotFoundException, SQLException {
		super();
		// TODO Auto-generated constructor stub
	}

	
	//------------------------------------------------------------------------
	// public interface
	//------------------------------------------------------------------------
	/**
	 * main method - generate index from database as configured in property file
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 * @throws CorruptIndexException 
	 */
	public static void main(String[] args) throws ClassNotFoundException, SQLException, CorruptIndexException, IOException {
		// configure jndi context
		JNDITestDatabaseBinder.bind();
		
		// create index
		LuceneGenerateResourceIndex<Bookmark> indexer = LuceneGenerateBookmarkIndex.getInstance();
		indexer.generateIndex();
		indexer.shutdown();
	}
	
	//------------------------------------------------------------------------
	// singleton pattern implementation
	//------------------------------------------------------------------------
	/**
	 * get readily configured instance 
	 */
	public static LuceneGenerateBookmarkIndex getInstance() {
		if( instance==null ) {			
			beanFactory = LuceneSpringContextWrapper.getBeanFactory();
			return (LuceneGenerateBookmarkIndex)beanFactory.getBean("luceneBookmarkIndexGenerator");
		} else
			return instance;
	}
	/**
	 * singelton instance for spring instantiation
	 */
	public static LuceneGenerateBookmarkIndex getPreInitInstance() throws ClassNotFoundException, SQLException {
		if( instance==null )
			return new LuceneGenerateBookmarkIndex();
		else
			return instance;
	}
	
	//------------------------------------------------------------------------
	// implementations for abstract methods
	//------------------------------------------------------------------------
	@Override
	protected Class<? extends Resource> getResourceType() {
		return Bookmark.class;
	}
}
