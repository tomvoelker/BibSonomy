package org.bibsonomy.lucene.util.generator;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.lucene.index.CorruptIndexException;
import org.bibsonomy.lucene.util.JNDITestDatabaseBinder;
import org.bibsonomy.lucene.util.LuceneSpringContextWrapper;
import org.bibsonomy.model.BibTex;

/**
 * generates lucene index for bibtex posts as configured in lucen.properties
 * 
 * @author fei
 * @version $Id$
 */
public class LuceneGenerateBibTexIndex extends LuceneGenerateResourceIndex<BibTex>{
	/** singleton pattern */
	private static LuceneGenerateBibTexIndex instance;
	
	/**
	 * @return the {@link LuceneGenerateBibTexIndex} instance (configured)
	 */
	public static LuceneGenerateBibTexIndex getInstance() {
		if (instance == null) {
			instance = new LuceneGenerateBibTexIndex();
			LuceneSpringContextWrapper.init();
		}
		
		return instance;
	}
	
	/**
	 * main method - generate index from database as configured in property file
	 * @param args 
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// configure jndi context
		JNDITestDatabaseBinder.bind();

		// create index
		run();
	}

	public static void run() throws CorruptIndexException, IOException, ClassNotFoundException, SQLException {
	    // FIXME: configure via spring
	    LuceneGenerateResourceIndex<BibTex> indexer = LuceneGenerateBibTexIndex.getInstance();
	    indexer.generateIndex();
	    indexer.shutdown();
	}
	
	private LuceneGenerateBibTexIndex() {
	}
	
	@Override
	protected String getResourceName() {
		return BibTex.class.getSimpleName();
	}	
}
