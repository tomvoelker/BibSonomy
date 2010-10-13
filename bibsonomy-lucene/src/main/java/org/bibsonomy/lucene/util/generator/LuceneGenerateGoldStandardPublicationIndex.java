package org.bibsonomy.lucene.util.generator;

import org.bibsonomy.lucene.util.JNDITestDatabaseBinder;
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
}