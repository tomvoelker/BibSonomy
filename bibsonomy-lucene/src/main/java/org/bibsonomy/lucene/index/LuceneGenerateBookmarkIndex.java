package org.bibsonomy.lucene.index;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.lucene.index.CorruptIndexException;
import org.bibsonomy.lucene.database.LuceneBibTexLogic;
import org.bibsonomy.lucene.database.LuceneBookmarkLogic;
import org.bibsonomy.lucene.util.JNDITestDatabaseBinder;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Resource;

public class LuceneGenerateBookmarkIndex extends LuceneGenerateResourceIndex<Bookmark> {

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
		JNDITestDatabaseBinder.bind();
		LuceneGenerateResourceIndex<Bookmark> indexer = new LuceneGenerateBookmarkIndex();
		indexer.setLogic(LuceneBookmarkLogic.getInstance());
		indexer.generateIndex();
		indexer.shutdown();
	}

	//------------------------------------------------------------------------
	// implementations for abstract methods
	//------------------------------------------------------------------------
	@Override
	protected Class<? extends Resource> getResourceType() {
		return Bookmark.class;
	}
}
