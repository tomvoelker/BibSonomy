package org.bibsonomy.lucene.index;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.lucene.index.CorruptIndexException;
import org.bibsonomy.lucene.database.LuceneBibTexLogic;
import org.bibsonomy.lucene.util.JNDITestDatabaseBinder;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Resource;

public class LuceneGenerateBibTexIndex extends LuceneGenerateResourceIndex<BibTex>{

	//------------------------------------------------------------------------
	// constructor
	//------------------------------------------------------------------------
	public LuceneGenerateBibTexIndex(Properties props)
	throws ClassNotFoundException, SQLException {
		super(props);
		// TODO Auto-generated constructor stub
	}
	
	public LuceneGenerateBibTexIndex()
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
		LuceneGenerateResourceIndex<BibTex> indexer = new LuceneGenerateBibTexIndex();
		indexer.setLogic(LuceneBibTexLogic.getInstance());
		DriverManager.setLogWriter(new PrintWriter(System.out));
		indexer.getLogic().getNumberOfPosts();
		indexer.generateIndex();
		indexer.shutdown();
	}
	
	//------------------------------------------------------------------------
	// implementations for abstract methods
	//------------------------------------------------------------------------
	@Override
	protected Class<? extends Resource> getResourceType() {
		return BibTex.class;
	}
	
}
