package org.bibsonomy.batch.searchlucene;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.LockObtainFailedException;
import org.bibsonomy.batch.searchlucene.database.LuceneLogicImpl;


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
		GenerateLuceneIndex indexer = new GenerateLuceneIndex();
		indexer.setLogic(LuceneLogicImpl.getInstance());
		indexer.createEmptyIndex();
		indexer.shutdown();
	}
	
}
