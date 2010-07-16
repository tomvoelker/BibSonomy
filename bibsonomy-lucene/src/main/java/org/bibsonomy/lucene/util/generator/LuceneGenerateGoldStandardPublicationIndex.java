package org.bibsonomy.lucene.util.generator;

import static org.bibsonomy.lucene.util.LuceneBase.SQL_BLOCKSIZE;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.CorruptIndexException;
import org.bibsonomy.lucene.param.LucenePost;
import org.bibsonomy.lucene.util.JNDITestDatabaseBinder;
import org.bibsonomy.lucene.util.LuceneBase;
import org.bibsonomy.lucene.util.LuceneSpringContextWrapper;
import org.bibsonomy.model.GoldStandardPublication;

/**
 * @author dzo
 * @version $Id$
 */
public class LuceneGenerateGoldStandardPublicationIndex extends LuceneGenerateResourceIndex<GoldStandardPublication> {

	private static LuceneGenerateGoldStandardPublicationIndex INSTANCE;

	/**
	 * @return the @{link:LuceneGenerateGoldstandardPublicationIndex} instance
	 */
	public static LuceneGenerateGoldStandardPublicationIndex getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new LuceneGenerateGoldStandardPublicationIndex();
			LuceneSpringContextWrapper.init();
		}
		
		return INSTANCE;
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
		LuceneGenerateResourceIndex<GoldStandardPublication> indexer = LuceneGenerateGoldStandardPublicationIndex.getInstance();
		indexer.generateIndex();
		indexer.shutdown();
	}
	
	private LuceneGenerateGoldStandardPublicationIndex() {
	}
	
	@Override
	protected String getResourceName() {
		return GoldStandardPublication.class.getSimpleName();
	}
	
	@Override
	public void createIndexFromDatabase() throws CorruptIndexException, IOException {
		final Date date = new Date();		
		log.info("Start writing data to lucene index");
		
		// read block wise all posts
		List<LucenePost<GoldStandardPublication>> postList = null;
		int offset = 0;
		do {
			postList = this.dbLogic.getPosts(offset, SQL_BLOCKSIZE, date);
			offset += postList.size();
			log.info("Read " + offset + " entries.");
			
			// cycle through all posts of currently read block
			for (final LucenePost<GoldStandardPublication> post : postList) {
				// create index document from post model
				final Document doc = this.getResourceConverter().readPost(post);
				
				// set update management fields
				doc.add(new Field(LuceneBase.FLD_LAST_DATE, String.valueOf(date.getTime()), Store.YES, Index.NOT_ANALYZED));
				doc.removeField(LuceneBase.FLD_LAST_LOG_DATE);
				doc.add(new Field(LuceneBase.FLD_LAST_LOG_DATE, String.valueOf(date.getTime()), Store.YES, Index.NOT_ANALYZED));
				
				this.indexWriter.addDocument(doc);
			}
			
			log.info("Ready.");
		} while (postList.size() == SQL_BLOCKSIZE);
		
		indexWriter.optimize();
		indexWriter.close();
	}
}