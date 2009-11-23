package org.bibsonomy.lucene.index;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.LockObtainFailedException;
import org.bibsonomy.lucene.database.LuceneBibTexLogic;
import org.bibsonomy.lucene.database.LuceneBookmarkLogic;
import org.bibsonomy.lucene.util.JNDITestDatabaseBinder;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;


/**
 * Generates empty index files for lucene. The path of the index files is configured 
 * in 'lucene.properties'
 *  
 * @author sst
 * @author fei
 */
public class GenerateEmptyLuceneIndex {

	public static void main(String[] args) throws CorruptIndexException, LockObtainFailedException, IOException, ClassNotFoundException, SQLException
	{
		// FIXME: move database configuration to a central place
		JNDITestDatabaseBinder.bind();
		
		LuceneGenerateResourceIndex<BibTex> bibTexIndexer = 
			LuceneGenerateBibTexIndex.getInstance(); 
		LuceneGenerateResourceIndex<Bookmark> bookmarkIndexer = 
			LuceneGenerateBookmarkIndex.getInstance();
		
		bibTexIndexer.createEmptyIndex();
		bibTexIndexer.shutdown();
		bookmarkIndexer.createEmptyIndex();
		bookmarkIndexer.shutdown();
	}
	
}
