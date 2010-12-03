package org.bibsonomy.lucene.util.generator;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.lucene.index.CorruptIndexException;
import org.bibsonomy.lucene.util.JNDITestDatabaseBinder;
import org.bibsonomy.lucene.util.LuceneSpringContextWrapper;
import org.bibsonomy.model.Bookmark;

/**
 * @author fei
 * @version $Id$
 */
public class LuceneGenerateBookmarkIndex extends LuceneGenerateResourceIndex<Bookmark> {
	/** singleton pattern */
	private static LuceneGenerateBookmarkIndex instance;
	
	/**
	 * @return the {@link LuceneGenerateBookmarkIndex} instance (configured)
	 */
	public static LuceneGenerateBookmarkIndex getInstance() {
		if (instance==null) {
			instance = new LuceneGenerateBookmarkIndex();
			LuceneSpringContextWrapper.init(); // inits the fields of the singleton
		}
		
		return instance;
	}
	
	/**
	 * main method - generate index from database as configured in property file
	 * 
	 * @param args 
	 * @throws Exception  
	 */
	public static void main(String[] args) throws Exception {
		// configure jndi context
		JNDITestDatabaseBinder.bind();
		
		// create index
	    LuceneGenerateResourceIndex<Bookmark> indexer = LuceneGenerateBookmarkIndex.getInstance();
	    indexer.generateIndex();
	    indexer.shutdown();
	}
	
	private LuceneGenerateBookmarkIndex() {
	}
	
	@Override
	protected String getResourceName() {
		return Bookmark.class.getSimpleName();
	}
}
